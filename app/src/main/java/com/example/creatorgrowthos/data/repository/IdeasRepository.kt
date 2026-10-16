package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.ContentIdeaDao
import com.example.creatorgrowthos.data.local.dao.ContentItemDao
import com.example.creatorgrowthos.data.local.dao.WorkflowAndChecklistDao
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.local.entity.WorkflowTransitionEntity
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class IdeasRepository(
    private val ideaDao: ContentIdeaDao,
    private val contentItemDao: ContentItemDao,
    private val workflowDao: WorkflowAndChecklistDao
) {
    fun getAllIdeas(): Flow<List<ContentIdeaEntity>> = ideaDao.getAllIdeas()

    fun getIdeasByStatus(status: String): Flow<List<ContentIdeaEntity>> = ideaDao.getIdeasByStatus(status)

    suspend fun getIdeaById(id: String): ContentIdeaEntity? = ideaDao.getIdeaById(id)

    suspend fun quickCapture(title: String, rawIdea: String = ""): ContentIdeaEntity {
        val id = UUID.randomUUID().toString()
        val idea = ContentIdeaEntity(
            id = id,
            title = title,
            rawIdea = rawIdea,
            demandScore = 6,
            demandExplanation = "Captured for validation",
            captureDateUtc = System.currentTimeMillis()
        )
        ideaDao.insertIdea(idea)
        return idea
    }

    suspend fun saveIdea(idea: ContentIdeaEntity) {
        ideaDao.insertIdea(idea)
    }

    suspend fun deleteIdea(id: String) {
        ideaDao.deleteIdea(id)
    }

    /**
     * Converts an idea into a planned content item without losing the original idea.
     */
    suspend fun convertIdeaToContentItem(
        ideaId: String,
        targetPlatforms: List<String> = emptyList(),
        priority: TaskPriority = TaskPriority.MEDIUM,
        productionDeadlineUtc: Long? = null,
        publicationDeadlineUtc: Long? = null
    ): ContentItemEntity? {
        val idea = ideaDao.getIdeaById(ideaId) ?: return null
        val contentId = UUID.randomUUID().toString()

        val contentItem = ContentItemEntity(
            id = contentId,
            title = idea.title,
            contentType = idea.format,
            contentPillarId = idea.contentPillarId,
            demandScore = idea.demandScore,
            priority = priority,
            status = WorkflowStage.PLANNED,
            thumbnailIdea = "Thumbnail for: ${idea.title}",
            productionDeadlineUtc = productionDeadlineUtc ?: (System.currentTimeMillis() + 86400000L * 3), // +3 days default
            publicationDeadlineUtc = publicationDeadlineUtc ?: (System.currentTimeMillis() + 86400000L * 5), // +5 days default
            nextAction = "Create script & visual outline",
            targetPlatforms = if (targetPlatforms.isNotEmpty()) targetPlatforms else listOfNotNull(idea.suggestedPlatform?.name),
            notes = "Converted from idea: ${idea.rawIdea}",
            originalIdeaId = idea.id
        )

        contentItemDao.insertContentItem(contentItem)
        ideaDao.markIdeaPromoted(idea.id, contentId)

        // Log initial workflow transition
        workflowDao.recordTransition(
            WorkflowTransitionEntity(
                id = UUID.randomUUID().toString(),
                contentItemId = contentId,
                fromStage = WorkflowStage.IDEA,
                toStage = WorkflowStage.PLANNED,
                transitionReason = "Idea promoted to production pipeline"
            )
        )

        return contentItem
    }
}
