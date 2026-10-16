package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ChecklistEntity
import com.example.creatorgrowthos.data.local.entity.ChecklistItemEntity
import com.example.creatorgrowthos.data.local.entity.WorkflowTransitionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkflowAndChecklistDao {
    // Workflow transitions
    @Query("SELECT * FROM workflow_transitions WHERE contentItemId = :contentItemId ORDER BY timestampUtc ASC")
    fun getTransitionsForContent(contentItemId: String): Flow<List<WorkflowTransitionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordTransition(transition: WorkflowTransitionEntity)

    // Checklists
    @Query("SELECT * FROM checklists WHERE targetId = :targetId LIMIT 1")
    suspend fun getChecklistForTarget(targetId: String): ChecklistEntity?

    @Query("SELECT * FROM checklists WHERE targetId = :targetId LIMIT 1")
    fun observeChecklistForTarget(targetId: String): Flow<ChecklistEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(checklist: ChecklistEntity)

    @Update
    suspend fun updateChecklist(checklist: ChecklistEntity)

    // Items
    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId ORDER BY itemOrder ASC")
    fun getChecklistItems(checklistId: String): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId ORDER BY itemOrder ASC")
    suspend fun getChecklistItemsList(checklistId: String): List<ChecklistItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateChecklistItem(item: ChecklistItemEntity)

    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :itemId")
    suspend fun setItemChecked(itemId: String, isChecked: Boolean)

    @Transaction
    suspend fun createChecklistWithItems(checklist: ChecklistEntity, items: List<ChecklistItemEntity>) {
        insertChecklist(checklist)
        insertChecklistItems(items)
    }
}
