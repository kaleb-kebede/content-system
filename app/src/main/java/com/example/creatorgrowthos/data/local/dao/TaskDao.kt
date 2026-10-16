package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM content_tasks ORDER BY scheduledStartTimeUtc ASC")
    fun getAllTasks(): Flow<List<ContentTaskEntity>>

    @Query("SELECT * FROM content_tasks WHERE id = :id")
    suspend fun getTaskById(id: String): ContentTaskEntity?

    @Query("SELECT * FROM content_tasks WHERE id = :id")
    fun observeTaskById(id: String): Flow<ContentTaskEntity?>

    @Query("SELECT * FROM content_tasks WHERE scheduledStartTimeUtc BETWEEN :startOfDayUtc AND :endOfDayUtc ORDER BY scheduledStartTimeUtc ASC")
    fun getTasksForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentTaskEntity>>

    @Query("SELECT * FROM content_tasks WHERE scheduledStartTimeUtc BETWEEN :startOfDayUtc AND :endOfDayUtc ORDER BY scheduledStartTimeUtc ASC")
    suspend fun getTasksForDayList(startOfDayUtc: Long, endOfDayUtc: Long): List<ContentTaskEntity>

    // Critical missed tasks requiring recovery decision
    @Query("SELECT * FROM content_tasks WHERE isCritical = 1 AND status = 'MISSED' ORDER BY scheduledStartTimeUtc ASC")
    fun getCriticalMissedTasks(): Flow<List<ContentTaskEntity>>

    @Query("SELECT * FROM content_tasks WHERE isCritical = 1 AND status = 'MISSED' ORDER BY scheduledStartTimeUtc ASC")
    suspend fun getCriticalMissedTasksList(): List<ContentTaskEntity>

    // Overdue tasks: scheduledEndTime < currentUtc and not completed/skipped
    @Query("SELECT * FROM content_tasks WHERE scheduledEndTimeUtc < :nowUtc AND status NOT IN ('COMPLETED', 'SKIPPED') ORDER BY priority DESC, scheduledEndTimeUtc ASC")
    fun getOverdueTasks(nowUtc: Long): Flow<List<ContentTaskEntity>>

    @Query("SELECT * FROM content_tasks WHERE scheduledEndTimeUtc < :nowUtc AND status NOT IN ('COMPLETED', 'SKIPPED') ORDER BY priority DESC, scheduledEndTimeUtc ASC")
    suspend fun getOverdueTasksList(nowUtc: Long): List<ContentTaskEntity>

    // Active task (currently in progress)
    @Query("SELECT * FROM content_tasks WHERE status = 'ACTIVE' LIMIT 1")
    fun observeActiveTask(): Flow<ContentTaskEntity?>

    @Query("SELECT * FROM content_tasks WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveTask(): ContentTaskEntity?

    // Upcoming tasks for today starting after now
    @Query("SELECT * FROM content_tasks WHERE scheduledStartTimeUtc > :nowUtc AND scheduledStartTimeUtc <= :endOfDayUtc AND status = 'UPCOMING' ORDER BY scheduledStartTimeUtc ASC LIMIT 1")
    fun observeNextUpcomingTask(nowUtc: Long, endOfDayUtc: Long): Flow<ContentTaskEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ContentTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<ContentTaskEntity>)

    @Update
    suspend fun updateTask(task: ContentTaskEntity)

    // Status transitions
    @Query("UPDATE content_tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: String, status: TaskStatus)

    @Query("UPDATE content_tasks SET status = 'COMPLETED', completionTimestampUtc = :completionTimeUtc, actualDurationMinutes = :actualDuration WHERE id = :id")
    suspend fun completeTask(id: String, completionTimeUtc: Long, actualDuration: Int)

    @Query("UPDATE content_tasks SET status = 'SKIPPED', skipReason = :reason WHERE id = :id")
    suspend fun skipTask(id: String, reason: String)

    @Query("UPDATE content_tasks SET status = 'BLOCKED', isBlocked = 1, blockerReason = :reason WHERE id = :id")
    suspend fun blockTask(id: String, reason: String)

    @Query("UPDATE content_tasks SET status = 'UPCOMING', isBlocked = 0, blockerReason = null WHERE id = :id")
    suspend fun resolveBlocker(id: String)

    @Query("UPDATE content_tasks SET scheduledStartTimeUtc = :newStartUtc, scheduledEndTimeUtc = :newEndUtc, rescheduleCount = rescheduleCount + 1, rescheduleReason = :reason, status = 'UPCOMING' WHERE id = :id")
    suspend fun rescheduleTask(id: String, newStartUtc: Long, newEndUtc: Long, reason: String)

    @Query("UPDATE content_tasks SET emergencyOverrideRecorded = 1, status = 'UPCOMING' WHERE id = :id")
    suspend fun recordEmergencyOverride(id: String)

    @Query("DELETE FROM content_tasks WHERE id = :id")
    suspend fun deleteTask(id: String)
}
