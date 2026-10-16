package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentItemDao {
    @Query("SELECT * FROM content_items ORDER BY updatedAtUtc DESC")
    fun getAllContentItems(): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE status = :stage ORDER BY priority DESC, publicationDeadlineUtc ASC")
    fun getContentItemsByStage(stage: WorkflowStage): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE id = :id")
    suspend fun getContentItemById(id: String): ContentItemEntity?

    @Query("SELECT * FROM content_items WHERE id = :id")
    fun observeContentItemById(id: String): Flow<ContentItemEntity?>

    @Query("SELECT * FROM content_items WHERE status NOT IN ('PUBLISHED', 'ARCHIVED', 'CANCELLED') ORDER BY publicationDeadlineUtc ASC")
    fun getActivePipelineItems(): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE publicationDeadlineUtc BETWEEN :startUtc AND :endUtc ORDER BY publicationDeadlineUtc ASC")
    fun getContentItemsWithDeadlineBetween(startUtc: Long, endUtc: Long): Flow<List<ContentItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentItem(item: ContentItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentItems(items: List<ContentItemEntity>)

    @Update
    suspend fun updateContentItem(item: ContentItemEntity)

    @Query("UPDATE content_items SET status = :newStage, updatedAtUtc = :timestampUtc WHERE id = :id")
    suspend fun updateStage(id: String, newStage: WorkflowStage, timestampUtc: Long = System.currentTimeMillis())

    @Query("DELETE FROM content_items WHERE id = :id")
    suspend fun deleteContentItem(id: String)

    @Query("SELECT COUNT(*) FROM content_items WHERE status = :stage")
    suspend fun countByStage(stage: WorkflowStage): Int
}
