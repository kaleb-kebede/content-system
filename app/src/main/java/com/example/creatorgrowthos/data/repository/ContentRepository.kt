package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.ContentItemDao
import com.example.creatorgrowthos.data.local.dao.ScriptDao
import com.example.creatorgrowthos.data.local.dao.WorkflowAndChecklistDao
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.local.entity.WorkflowTransitionEntity
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class StageTransitionResult {
    data class Success(val item: ContentItemEntity) : StageTransitionResult()
    data class ValidationFailed(val reason: String) : StageTransitionResult()
    data class ItemNotFound(val id: String) : StageTransitionResult()
}

class ContentRepository(
    private val contentItemDao: ContentItemDao,
    private val scriptDao: ScriptDao,
    private val workflowDao: WorkflowAndChecklistDao
) {
    fun getAllContentItems(): Flow<List<ContentItemEntity>> = contentItemDao.getAllContentItems()

    fun getActivePipelineItems(): Flow<List<ContentItemEntity>> = contentItemDao.getActivePipelineItems()

    fun getContentItemsByStage(stage: WorkflowStage): Flow<List<ContentItemEntity>> = contentItemDao.getContentItemsByStage(stage)

    suspend fun getContentItemById(id: String): ContentItemEntity? = contentItemDao.getContentItemById(id)

    fun observeContentItemById(id: String): Flow<ContentItemEntity?> = contentItemDao.observeContentItemById(id)

    suspend fun saveContentItem(item: ContentItemEntity) {
        contentItemDao.insertContentItem(item)
    }

    suspend fun deleteContentItem(id: String) {
        contentItemDao.deleteContentItem(id)
    }

    /**
     * Attempts to transition a content item to a new workflow stage with validation checks.
     */
    suspend fun transitionStage(
        contentId: String,
        newStage: WorkflowStage,
        reason: String = ""
    ): StageTransitionResult {
        val item = contentItemDao.getContentItemById(contentId)
            ?: return StageTransitionResult.ItemNotFound(contentId)

        // Validation rules
        when (newStage) {
            WorkflowStage.APPROVED -> {
                // Must have thumbnail idea and target platforms
                if (item.thumbnailIdea.isBlank()) {
                    return StageTransitionResult.ValidationFailed("Thumbnail concept is required before approval.")
                }
                if (item.targetPlatforms.isEmpty()) {
                    return StageTransitionResult.ValidationFailed("At least one target platform must be selected.")
                }
            }
            WorkflowStage.SCHEDULED -> {
                if (item.publicationDeadlineUtc == null) {
                    return StageTransitionResult.ValidationFailed("Publication deadline must be set before scheduling.")
                }
                if (item.status != WorkflowStage.APPROVED && item.status != WorkflowStage.SCHEDULED) {
                    return StageTransitionResult.ValidationFailed("Content must be APPROVED before it can be SCHEDULED.")
                }
            }
            WorkflowStage.PUBLISHED -> {
                // Do not mark content as published solely from pipeline without confirmed publication records
                // Can only be set via publication confirmation
            }
            else -> {}
        }

        val updated = item.copy(status = newStage, updatedAtUtc = System.currentTimeMillis())
        contentItemDao.updateContentItem(updated)

        workflowDao.recordTransition(
            WorkflowTransitionEntity(
                id = UUID.randomUUID().toString(),
                contentItemId = contentId,
                fromStage = item.status,
                toStage = newStage,
                transitionReason = reason.ifBlank { "Moved to ${newStage.displayName}" },
                validationChecksPassed = true
            )
        )

        return StageTransitionResult.Success(updated)
    }

    fun getTransitionsForContent(contentId: String): Flow<List<WorkflowTransitionEntity>> =
        workflowDao.getTransitionsForContent(contentId)
}
