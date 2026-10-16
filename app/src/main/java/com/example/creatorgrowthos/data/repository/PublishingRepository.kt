package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.PlatformAccountDao
import com.example.creatorgrowthos.data.local.dao.PublicationDao
import com.example.creatorgrowthos.data.local.dao.TaskDao
import com.example.creatorgrowthos.data.local.dao.WorkflowAndChecklistDao
import com.example.creatorgrowthos.data.local.entity.ChecklistEntity
import com.example.creatorgrowthos.data.local.entity.ChecklistItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.UUID

data class PlatformWeeklyProgress(
    val platform: PlatformType,
    val weeklyTarget: Int,
    val confirmedPublished: Int,
    val remaining: Int,
    val completionPercentage: Float
)

data class WeeklyContentPlanSummary(
    val totalTarget: Int,
    val totalConfirmed: Int,
    val totalRemaining: Int,
    val overallPercentage: Float,
    val platformBreakdown: List<PlatformWeeklyProgress>
)

class PublishingRepository(
    private val publicationDao: PublicationDao,
    private val platformAccountDao: PlatformAccountDao,
    private val taskDao: TaskDao,
    private val workflowDao: WorkflowAndChecklistDao
) {
    fun getAllPublications(): Flow<List<ContentPublicationEntity>> = publicationDao.getAllPublications()

    suspend fun getPublicationById(id: String): ContentPublicationEntity? = publicationDao.getPublicationById(id)

    fun observePublicationById(id: String): Flow<ContentPublicationEntity?> = publicationDao.observePublicationById(id)

    fun getPublicationsScheduledForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentPublicationEntity>> =
        publicationDao.getPublicationsScheduledForDay(startOfDayUtc, endOfDayUtc)

    fun getPublicationsAwaitingConfirmation(): Flow<List<ContentPublicationEntity>> =
        publicationDao.getPublicationsAwaitingConfirmation()

    suspend fun schedulePublication(publication: ContentPublicationEntity): ContentPublicationEntity {
        publicationDao.insertPublication(publication)

        // Create standard publication checklist
        val checklistId = UUID.randomUUID().toString()
        val checklist = ChecklistEntity(
            id = checklistId,
            targetType = "PUBLICATION",
            targetId = publication.id,
            title = "Publication Checklist: ${publication.destination.displayName}"
        )
        val items = listOf(
            ChecklistItemEntity(UUID.randomUUID().toString(), checklistId, "Verify video title and caption hook", isChecked = false, itemOrder = 0),
            ChecklistItemEntity(UUID.randomUUID().toString(), checklistId, "Check thumbnail / cover frame", isChecked = false, itemOrder = 1),
            ChecklistItemEntity(UUID.randomUUID().toString(), checklistId, "Verify platform hashtags & tags", isChecked = false, itemOrder = 2),
            ChecklistItemEntity(UUID.randomUUID().toString(), checklistId, "Open ${publication.destination.displayName} upload screen", isChecked = false, itemOrder = 3),
            ChecklistItemEntity(UUID.randomUUID().toString(), checklistId, "Publish post and copy published URL", isChecked = false, itemOrder = 4)
        )
        workflowDao.createChecklistWithItems(checklist, items)

        return publication
    }

    suspend fun updatePublication(publication: ContentPublicationEntity) {
        publicationDao.updatePublication(publication)
    }

    /**
     * Confirms that publication actually completed, saving URL, time, and scheduling follow-up analytics review.
     */
    suspend fun confirmPublication(
        publicationId: String,
        publishedUrl: String,
        actualTimeUtc: Long = System.currentTimeMillis()
    ) {
        publicationDao.confirmPublication(
            id = publicationId,
            actualTimeUtc = actualTimeUtc,
            url = publishedUrl,
            status = PublicationStatus.PUBLISHED_CONFIRMED
        )

        val pub = publicationDao.getPublicationById(publicationId) ?: return

        // Schedule analytics follow-up task 48 hours later (Section 14 & 15)
        val followUpStart = actualTimeUtc + 48 * 3600 * 1000L
        val followUpTask = ContentTaskEntity(
            id = UUID.randomUUID().toString(),
            title = "Analytics 48h Review: ${pub.destination.displayName}",
            description = "Log 48-hour performance metrics for: $publishedUrl",
            linkedContentItemId = pub.contentItemId,
            workflowStage = WorkflowStage.ANALYZING,
            scheduledStartTimeUtc = followUpStart,
            scheduledEndTimeUtc = followUpStart + 15 * 60 * 1000L,
            priority = TaskPriority.MEDIUM,
            isCritical = false,
            status = TaskStatus.UPCOMING
        )
        taskDao.insertTask(followUpTask)
    }

    /**
     * Calculates weekly publishing progress strictly from confirmed posts (Section 9).
     */
    suspend fun getWeeklyPublishingSummary(weekStartUtc: Long, weekEndUtc: Long): WeeklyContentPlanSummary {
        val accounts = platformAccountDao.getAllAccountsList()
        val breakdowns = mutableListOf<PlatformWeeklyProgress>()

        var totalTarget = 0
        var totalConfirmed = 0

        for (acc in accounts) {
            val confirmed = publicationDao.getConfirmedPublicationsForDestination(
                acc.platform,
                weekStartUtc,
                weekEndUtc
            ).size

            val target = acc.weeklyPublishingTarget
            val remaining = (target - confirmed).coerceAtLeast(0)
            val pct = if (target > 0) (confirmed.toFloat() / target * 100f).coerceAtMost(100f) else 100f

            totalTarget += target
            totalConfirmed += confirmed

            breakdowns.add(
                PlatformWeeklyProgress(
                    platform = acc.platform,
                    weeklyTarget = target,
                    confirmedPublished = confirmed,
                    remaining = remaining,
                    completionPercentage = pct
                )
            )
        }

        val totalRemaining = (totalTarget - totalConfirmed).coerceAtLeast(0)
        val overallPct = if (totalTarget > 0) (totalConfirmed.toFloat() / totalTarget * 100f).coerceAtMost(100f) else 0f

        return WeeklyContentPlanSummary(
            totalTarget = totalTarget,
            totalConfirmed = totalConfirmed,
            totalRemaining = totalRemaining,
            overallPercentage = overallPct,
            platformBreakdown = breakdowns
        )
    }

    fun getChecklistForPublication(publicationId: String): Flow<ChecklistEntity?> =
        workflowDao.observeChecklistForTarget(publicationId)

    fun getChecklistItems(checklistId: String): Flow<List<ChecklistItemEntity>> =
        workflowDao.getChecklistItems(checklistId)

    suspend fun setChecklistItemChecked(itemId: String, isChecked: Boolean) {
        workflowDao.setItemChecked(itemId, isChecked)
    }
}
