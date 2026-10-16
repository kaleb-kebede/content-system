package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.RoutineInstanceEntity
import com.example.creatorgrowthos.data.local.entity.RoutineTemplateEntity
import com.example.creatorgrowthos.data.local.entity.ScheduleBlockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBlockDao {
    @Query("SELECT * FROM schedule_blocks WHERE startTimeUtc BETWEEN :startUtc AND :endUtc ORDER BY startTimeUtc ASC")
    fun getBlocksBetween(startUtc: Long, endUtc: Long): Flow<List<ScheduleBlockEntity>>

    @Query("SELECT * FROM schedule_blocks WHERE (startTimeUtc < :endTimeUtc AND endTimeUtc > :startTimeUtc)")
    suspend fun findOverlappingBlocks(startTimeUtc: Long, endTimeUtc: Long): List<ScheduleBlockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: ScheduleBlockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<ScheduleBlockEntity>)

    @Query("DELETE FROM schedule_blocks WHERE id = :id")
    suspend fun deleteBlock(id: String)

    @Query("DELETE FROM schedule_blocks WHERE taskId = :taskId")
    suspend fun deleteBlocksForTask(taskId: String)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine_templates WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveTemplate(): RoutineTemplateEntity?

    @Query("SELECT * FROM routine_templates WHERE isActive = 1 LIMIT 1")
    fun observeActiveTemplate(): Flow<RoutineTemplateEntity?>

    @Query("SELECT * FROM routine_templates")
    fun getAllTemplates(): Flow<List<RoutineTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: RoutineTemplateEntity)

    @Update
    suspend fun updateTemplate(template: RoutineTemplateEntity)

    // Daily Instances
    @Query("SELECT * FROM routine_instances WHERE dateString = :dateString LIMIT 1")
    suspend fun getInstanceForDate(dateString: String): RoutineInstanceEntity?

    @Query("SELECT * FROM routine_instances WHERE dateString = :dateString LIMIT 1")
    fun observeInstanceForDate(dateString: String): Flow<RoutineInstanceEntity?>

    @Query("SELECT * FROM routine_instances ORDER BY startOfDayUtc DESC LIMIT 30")
    fun getRecentInstances(): Flow<List<RoutineInstanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstance(instance: RoutineInstanceEntity)

    @Update
    suspend fun updateInstance(instance: RoutineInstanceEntity)
}
