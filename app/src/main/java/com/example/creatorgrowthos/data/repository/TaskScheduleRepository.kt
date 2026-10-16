package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.RoutineDao
import com.example.creatorgrowthos.data.local.dao.ScheduleBlockDao
import com.example.creatorgrowthos.data.local.dao.TaskDao
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.local.entity.RoutineInstanceEntity
import com.example.creatorgrowthos.data.local.entity.ScheduleBlockEntity
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.domain.model.WorkflowStage
import com.example.creatorgrowthos.domain.scheduler.RecoveryOption
import com.example.creatorgrowthos.domain.scheduler.SchedulingEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Calendar
import java.util.UUID

class TaskScheduleRepository(
    private val taskDao: TaskDao,
    private val blockDao: ScheduleBlockDao,
    private val routineDao: RoutineDao,
    private val schedulingEngine: SchedulingEngine
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun getAllTasks(): Flow<List<ContentTaskEntity>> = taskDao.getAllTasks()

    fun getTodayTasks(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentTaskEntity>> =
        taskDao.getTasksForDay(startOfDayUtc, endOfDayUtc)

    fun observeActiveTask(): Flow<ContentTaskEntity?> = taskDao.observeActiveTask()

    fun observeNextUpcomingTask(nowUtc: Long, endOfDayUtc: Long): Flow<ContentTaskEntity?> =
        taskDao.observeNextUpcomingTask(nowUtc, endOfDayUtc)

    fun getCriticalMissedTasks(): Flow<List<ContentTaskEntity>> = taskDao.getCriticalMissedTasks()

    fun getOverdueTasks(nowUtc: Long = System.currentTimeMillis()): Flow<List<ContentTaskEntity>> =
        taskDao.getOverdueTasks(nowUtc)

    suspend fun getTaskById(taskId: String): ContentTaskEntity? = taskDao.getTaskById(taskId)

    suspend fun saveTask(task: ContentTaskEntity) {
        taskDao.insertTask(task)
        // Sync schedule block
        blockDao.insertBlock(
            ScheduleBlockEntity(
                id = "block_${task.id}",
                blockType = "ROUTINE_TASK",
                title = task.title,
                startTimeUtc = task.scheduledStartTimeUtc,
                endTimeUtc = task.scheduledEndTimeUtc,
                taskId = task.id
            )
        )
    }

    suspend fun startTask(taskId: String) {
        // Pause any currently active task first
        val currentActive = taskDao.getActiveTask()
        if (currentActive != null && currentActive.id != taskId) {
            taskDao.updateTaskStatus(currentActive.id, TaskStatus.PAUSED)
        }
        taskDao.updateTaskStatus(taskId, TaskStatus.ACTIVE)
    }

    suspend fun pauseTask(taskId: String) {
        taskDao.updateTaskStatus(taskId, TaskStatus.PAUSED)
    }

    suspend fun resumeTask(taskId: String) {
        startTask(taskId)
    }

    suspend fun completeTask(taskId: String, actualDurationMinutes: Int) {
        val now = System.currentTimeMillis()
        taskDao.completeTask(taskId, now, actualDurationMinutes)
    }

    suspend fun skipTask(taskId: String, reason: String) {
        taskDao.skipTask(taskId, reason)
    }

    suspend fun blockTask(taskId: String, reason: String) {
        taskDao.blockTask(taskId, reason)
    }

    suspend fun resolveBlocker(taskId: String) {
        taskDao.resolveBlocker(taskId)
    }

    suspend fun rescheduleTask(taskId: String, newStartUtc: Long, newEndUtc: Long, reason: String) {
        taskDao.rescheduleTask(taskId, newStartUtc, newEndUtc, reason)
        // Update schedule block
        blockDao.insertBlock(
            ScheduleBlockEntity(
                id = "block_$taskId",
                blockType = "ROUTINE_TASK",
                title = taskDao.getTaskById(taskId)?.title ?: "Task",
                startTimeUtc = newStartUtc,
                endTimeUtc = newEndUtc,
                taskId = taskId
            )
        )
    }

    suspend fun splitTask(
        taskId: String,
        part1DurationMinutes: Int,
        part2DurationMinutes: Int,
        part2StartUtc: Long
    ) {
        val original = taskDao.getTaskById(taskId) ?: return
        val part1End = original.scheduledStartTimeUtc + part1DurationMinutes * 60 * 1000L

        // Update Part 1 (original task)
        val part1 = original.copy(
            title = "${original.title} (Part 1)",
            estimatedDurationMinutes = part1DurationMinutes,
            scheduledEndTimeUtc = part1End
        )
        taskDao.updateTask(part1)

        // Create Part 2
        val part2Id = UUID.randomUUID().toString()
        val part2 = original.copy(
            id = part2Id,
            title = "${original.title} (Part 2)",
            estimatedDurationMinutes = part2DurationMinutes,
            scheduledStartTimeUtc = part2StartUtc,
            scheduledEndTimeUtc = part2StartUtc + part2DurationMinutes * 60 * 1000L,
            dependencyTaskIds = original.dependencyTaskIds + original.id
        )
        saveTask(part2)
    }

    suspend fun recordEmergencyOverride(taskId: String) {
        taskDao.recordEmergencyOverride(taskId)
    }

    suspend fun getRecoveryOptionsForTask(taskId: String): List<RecoveryOption> {
        val task = taskDao.getTaskById(taskId) ?: return emptyList()
        val existingBlocks = blockDao.findOverlappingBlocks(
            System.currentTimeMillis(),
            System.currentTimeMillis() + 86400000L * 2
        )
        return schedulingEngine.generateRecoveryPlan(task, existingBlocks)
    }

    /**
     * Checks if routine tasks for today have been instantiated. If not, generates them from the active template.
     */
    suspend fun ensureDailyRoutineGenerated(
        dateString: String,
        startOfDayUtc: Long
    ) {
        val existingInstance = routineDao.getInstanceForDate(dateString)
        if (existingInstance != null) return

        val template = routineDao.getActiveTemplate() ?: return
        val newTasks = mutableListOf<ContentTaskEntity>()
        val newBlocks = mutableListOf<ScheduleBlockEntity>()

        try {
            val element = json.parseToJsonElement(template.itemsJson)
            val items = element.jsonArray

            for (item in items) {
                val obj = item.jsonObject
                val startMin = obj["startMin"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
                val durationMin = obj["durationMin"]?.jsonPrimitive?.content?.toIntOrNull() ?: 30
                val title = obj["title"]?.jsonPrimitive?.content ?: "Routine Session"
                val critical = obj["critical"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
                val stageStr = obj["workflowStage"]?.jsonPrimitive?.content
                val stage = if (stageStr != null) try { WorkflowStage.valueOf(stageStr) } catch (e: Exception) { null } else null

                val taskId = UUID.randomUUID().toString()
                val taskStart = startOfDayUtc + (template.workStartHour * 60 + template.workStartMinute + startMin) * 60 * 1000L
                val taskEnd = taskStart + durationMin * 60 * 1000L

                val task = ContentTaskEntity(
                    id = taskId,
                    title = title,
                    workflowStage = stage,
                    scheduledStartTimeUtc = taskStart,
                    scheduledEndTimeUtc = taskEnd,
                    estimatedDurationMinutes = durationMin,
                    priority = if (critical) TaskPriority.CRITICAL else TaskPriority.MEDIUM,
                    isCritical = critical,
                    status = TaskStatus.UPCOMING
                )
                newTasks.add(task)
                newBlocks.add(
                    ScheduleBlockEntity(
                        id = "block_$taskId",
                        blockType = "ROUTINE_TASK",
                        title = title,
                        startTimeUtc = taskStart,
                        endTimeUtc = taskEnd,
                        taskId = taskId
                    )
                )
            }

            taskDao.insertTasks(newTasks)
            blockDao.insertBlocks(newBlocks)

            routineDao.insertInstance(
                RoutineInstanceEntity(
                    id = UUID.randomUUID().toString(),
                    templateId = template.id,
                    dateString = dateString,
                    startOfDayUtc = startOfDayUtc,
                    totalTasksPlanned = newTasks.size,
                    tasksCompleted = 0,
                    completionPercentage = 0f
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Checks for overdue tasks and flags them as MISSED or OVERDUE according to strict enforcement.
     */
    suspend fun auditTaskStatus(nowUtc: Long = System.currentTimeMillis()) {
        val overdueTasks = taskDao.getOverdueTasksList(nowUtc)
        for (task in overdueTasks) {
            if (task.isCritical && task.status != TaskStatus.MISSED) {
                taskDao.updateTaskStatus(task.id, TaskStatus.MISSED)
            } else if (!task.isCritical && task.status == TaskStatus.UPCOMING) {
                taskDao.updateTaskStatus(task.id, TaskStatus.OVERDUE)
            }
        }
    }
}
