package com.example.creatorgrowthos.domain.scheduler

import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.local.entity.ScheduleBlockEntity
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import java.util.Calendar
import java.util.TimeZone

data class ScheduleSlot(
    val startUtc: Long,
    val endUtc: Long
)

data class SchedulingConflict(
    val conflictingTask: ContentTaskEntity,
    val existingBlock: ScheduleBlockEntity?,
    val conflictReason: String
)

data class DependencyViolation(
    val dependentTask: ContentTaskEntity,
    val prerequisiteTaskId: String,
    val reason: String
)

data class RecoveryOption(
    val actionType: String, // RESCHEDULE, SPLIT, MOVE_DEPENDENCIES, MARK_BLOCKED, SKIP
    val title: String,
    val description: String,
    val suggestedStartUtc: Long? = null,
    val suggestedEndUtc: Long? = null
)

class SchedulingEngine {

    /**
     * Detects overlapping schedule blocks and tasks.
     */
    fun detectConflicts(
        targetTask: ContentTaskEntity,
        existingBlocks: List<ScheduleBlockEntity>
    ): List<SchedulingConflict> {
        val conflicts = mutableListOf<SchedulingConflict>()
        for (block in existingBlocks) {
            if (block.taskId == targetTask.id) continue
            // Overlap check: start < other.end && end > other.start
            if (targetTask.scheduledStartTimeUtc < block.endTimeUtc && targetTask.scheduledEndTimeUtc > block.startTimeUtc) {
                conflicts.add(
                    SchedulingConflict(
                        conflictingTask = targetTask,
                        existingBlock = block,
                        conflictReason = "Overlaps with '${block.title}' (${formatTimeRange(block.startTimeUtc, block.endTimeUtc)})"
                    )
                )
            }
        }
        return conflicts
    }

    /**
     * Validates that all prerequisites for a task have completed before it starts.
     */
    fun checkDependencyViolations(
        task: ContentTaskEntity,
        allTasksMap: Map<String, ContentTaskEntity>
    ): List<DependencyViolation> {
        val violations = mutableListOf<DependencyViolation>()
        for (depId in task.dependencyTaskIds) {
            val prereq = allTasksMap[depId]
            if (prereq == null) {
                violations.add(DependencyViolation(task, depId, "Required dependency task $depId does not exist"))
            } else if (prereq.status != TaskStatus.COMPLETED) {
                if (prereq.scheduledEndTimeUtc > task.scheduledStartTimeUtc) {
                    violations.add(
                        DependencyViolation(
                            task,
                            depId,
                            "Prerequisite '${prereq.title}' is scheduled to finish after '${task.title}' starts"
                        )
                    )
                }
            }
        }
        return violations
    }

    /**
     * Searches for the next available valid time slot within working hours.
     */
    fun findNextAvailableSlot(
        durationMinutes: Int,
        earliestStartUtc: Long,
        existingBlocks: List<ScheduleBlockEntity>,
        workStartHour: Int = 7,
        workEndHour: Int = 17,
        bufferMinutes: Int = 10,
        timeZone: TimeZone = TimeZone.getDefault()
    ): ScheduleSlot {
        val durationMillis = durationMinutes * 60 * 1000L
        val bufferMillis = bufferMinutes * 60 * 1000L
        val sortedBlocks = existingBlocks.sortedBy { it.startTimeUtc }

        var candidateStart = earliestStartUtc
        val cal = Calendar.getInstance(timeZone)

        // Adjust to working hours window
        cal.timeInMillis = candidateStart
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        if (hour < workStartHour) {
            cal.set(Calendar.HOUR_OF_DAY, workStartHour)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            candidateStart = cal.timeInMillis
        } else if (hour >= workEndHour) {
            // Move to next working day
            cal.add(Calendar.DAY_OF_YEAR, 1)
            cal.set(Calendar.HOUR_OF_DAY, workStartHour)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            candidateStart = cal.timeInMillis
        }

        while (true) {
            val candidateEnd = candidateStart + durationMillis

            // Ensure slot ends before work day end
            cal.timeInMillis = candidateStart
            cal.set(Calendar.HOUR_OF_DAY, workEndHour)
            cal.set(Calendar.MINUTE, 0)
            val dayEndUtc = cal.timeInMillis

            if (candidateEnd > dayEndUtc) {
                // Move to next day
                cal.timeInMillis = candidateStart
                cal.add(Calendar.DAY_OF_YEAR, 1)
                cal.set(Calendar.HOUR_OF_DAY, workStartHour)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                candidateStart = cal.timeInMillis
                continue
            }

            // Check overlap with any existing block
            var hasOverlap = false
            for (block in sortedBlocks) {
                val blockStartWithBuffer = block.startTimeUtc - bufferMillis
                val blockEndWithBuffer = block.endTimeUtc + bufferMillis
                if (candidateStart < blockEndWithBuffer && candidateEnd > blockStartWithBuffer) {
                    hasOverlap = true
                    candidateStart = blockEndWithBuffer
                    break
                }
            }

            if (!hasOverlap) {
                return ScheduleSlot(candidateStart, candidateEnd)
            }
        }
    }

    /**
     * Generates recovery options for missed or blocked critical tasks.
     */
    fun generateRecoveryPlan(
        missedTask: ContentTaskEntity,
        existingBlocks: List<ScheduleBlockEntity>,
        dependentTasks: List<ContentTaskEntity> = emptyList(),
        workStartHour: Int = 7,
        workEndHour: Int = 17
    ): List<RecoveryOption> {
        val options = mutableListOf<RecoveryOption>()

        // 1. Reschedule to next slot
        val nextSlot = findNextAvailableSlot(
            durationMinutes = missedTask.estimatedDurationMinutes,
            earliestStartUtc = System.currentTimeMillis() + 5 * 60 * 1000L, // in 5 mins
            existingBlocks = existingBlocks,
            workStartHour = workStartHour,
            workEndHour = workEndHour
        )
        options.add(
            RecoveryOption(
                actionType = "RESCHEDULE",
                title = "Reschedule to Next Available Slot",
                description = "Move to ${formatDateTime(nextSlot.startUtc)} (${missedTask.estimatedDurationMinutes}m)",
                suggestedStartUtc = nextSlot.startUtc,
                suggestedEndUtc = nextSlot.endUtc
            )
        )

        // 2. Split into smaller chunks if > 30 mins
        if (missedTask.estimatedDurationMinutes > 30) {
            val halfDuration = missedTask.estimatedDurationMinutes / 2
            val firstSlot = findNextAvailableSlot(
                durationMinutes = halfDuration,
                earliestStartUtc = System.currentTimeMillis() + 5 * 60 * 1000L,
                existingBlocks = existingBlocks,
                workStartHour = workStartHour,
                workEndHour = workEndHour
            )
            options.add(
                RecoveryOption(
                    actionType = "SPLIT",
                    title = "Split into Two ${halfDuration}m Sessions",
                    description = "Execute Part 1 at ${formatTime(firstSlot.startUtc)} and schedule Part 2 later",
                    suggestedStartUtc = firstSlot.startUtc,
                    suggestedEndUtc = firstSlot.endUtc
                )
            )
        }

        // 3. Move dependent tasks if any
        if (dependentTasks.isNotEmpty()) {
            options.add(
                RecoveryOption(
                    actionType = "MOVE_DEPENDENCIES",
                    title = "Cascade & Push ${dependentTasks.size} Dependent Tasks",
                    description = "Push following tasks by ${missedTask.estimatedDurationMinutes}m to maintain sequence"
                )
            )
        }

        // 4. Mark Blocked with reason
        options.add(
            RecoveryOption(
                actionType = "MARK_BLOCKED",
                title = "Mark as Blocked",
                description = "Record external dependency/impediment without losing task history"
            )
        )

        // 5. Skip with recorded reason
        options.add(
            RecoveryOption(
                actionType = "SKIP",
                title = "Skip with Justified Reason",
                description = "Explicitly archive this session with a documented reason"
            )
        )

        return options
    }

    /**
     * Deterministic sorting policy (Section 7):
     * 1. Overdue critical tasks
     * 2. Critical tasks due today
     * 3. Tasks blocking publication
     * 4. Tasks due today
     * 5. Tasks supporting weekly publishing targets
     * 6. Strategic and improvement work
     */
    fun sortTasksByPriorityOrder(
        tasks: List<ContentTaskEntity>,
        nowUtc: Long = System.currentTimeMillis(),
        endOfDayUtc: Long = nowUtc + 86400000L
    ): List<ContentTaskEntity> {
        return tasks.sortedWith(
            compareBy<ContentTaskEntity> { task ->
                when {
                    task.isCritical && task.scheduledEndTimeUtc < nowUtc && !task.status.isFinal() -> 0
                    task.isCritical && task.scheduledEndTimeUtc <= endOfDayUtc && !task.status.isFinal() -> 1
                    task.priority == TaskPriority.CRITICAL -> 2
                    task.deadlineUtc != null && task.deadlineUtc <= endOfDayUtc -> 3
                    task.priority == TaskPriority.HIGH -> 4
                    task.priority == TaskPriority.MEDIUM -> 5
                    else -> 6
                }
            }.thenBy { it.scheduledStartTimeUtc }
        )
    }

    private fun formatTime(utc: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = utc
        return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
    }

    private fun formatTimeRange(startUtc: Long, endUtc: Long): String {
        return "${formatTime(startUtc)} - ${formatTime(endUtc)}"
    }

    private fun formatDateTime(utc: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = utc
        return "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.DAY_OF_MONTH)} at ${formatTime(utc)}"
    }
}
