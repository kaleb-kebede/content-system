package com.example.creatorgrowthos

import com.example.creatorgrowthos.data.local.dao.PlatformAccountDao
import com.example.creatorgrowthos.data.local.dao.PublicationDao
import com.example.creatorgrowthos.data.local.dao.TaskDao
import com.example.creatorgrowthos.data.local.dao.WorkflowAndChecklistDao
import com.example.creatorgrowthos.data.local.entity.ChecklistEntity
import com.example.creatorgrowthos.data.local.entity.ChecklistItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.data.local.entity.WorkflowTransitionEntity
import com.example.creatorgrowthos.data.repository.PublishingRepository
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import com.example.creatorgrowthos.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PublishingSummaryTest {

    private lateinit var publishingRepository: PublishingRepository
    private val fakePublicationDao = FakePublicationDao()
    private val fakeAccountDao = FakePlatformAccountDao()
    private val fakeTaskDao = FakeTaskDao()
    private val fakeWorkflowDao = FakeWorkflowChecklistDao()

    @Before
    fun setup() {
        publishingRepository = PublishingRepository(
            fakePublicationDao,
            fakeAccountDao,
            fakeTaskDao,
            fakeWorkflowDao
        )
    }

    @Test
    fun confirmPublication_recordsDetailsAndCreatesAnalyticsFollowupTask() = runTest {
        val pub = ContentPublicationEntity(
            id = "pub_1",
            contentItemId = "content_1",
            destination = PlatformType.YOUTUBE_LONG,
            plannedPublicationUtc = System.currentTimeMillis(),
            publicationStatus = PublicationStatus.SCHEDULED_IN_APP
        )
        fakePublicationDao.insertPublication(pub)

        val publishedTime = System.currentTimeMillis()
        val publishedUrl = "https://youtube.com/watch?v=growth_os_123"

        publishingRepository.confirmPublication("pub_1", publishedUrl, publishedTime)

        val confirmedPub = fakePublicationDao.getPublicationById("pub_1")
        assertEquals(PublicationStatus.PUBLISHED_CONFIRMED, confirmedPub?.publicationStatus)
        assertEquals(publishedUrl, confirmedPub?.publishedUrl)
        assertEquals(publishedTime, confirmedPub?.actualPublicationUtc)

        // Verifies that analytics follow-up task is scheduled 48h later
        assertEquals(1, fakeTaskDao.tasks.size)
        val followUp = fakeTaskDao.tasks.values.first()
        assertTrue(followUp.title.contains("Analytics 48h Review"))
        assertEquals(publishedTime + 48 * 3600 * 1000L, followUp.scheduledStartTimeUtc)
    }

    @Test
    fun weeklySummary_onlyCountsConfirmedPublishedPosts() = runTest {
        val weekStart = 100000L
        val weekEnd = 200000L

        // Accounts: YT Shorts (5), TikTok (5), IG Reels (3), YT Long (1) -> Total 14
        fakeAccountDao.accounts.add(PlatformAccountEntity("acc_1", PlatformType.YOUTUBE_SHORTS, "Shorts", weeklyPublishingTarget = 5))
        fakeAccountDao.accounts.add(PlatformAccountEntity("acc_2", PlatformType.TIKTOK, "TikTok", weeklyPublishingTarget = 5))
        fakeAccountDao.accounts.add(PlatformAccountEntity("acc_3", PlatformType.INSTAGRAM_REELS, "Reels", weeklyPublishingTarget = 3))
        fakeAccountDao.accounts.add(PlatformAccountEntity("acc_4", PlatformType.YOUTUBE_LONG, "YT Long", weeklyPublishingTarget = 1))

        // Post 1: Confirmed YT Shorts
        fakePublicationDao.insertPublication(
            ContentPublicationEntity(
                id = "p1",
                contentItemId = "c1",
                destination = PlatformType.YOUTUBE_SHORTS,
                plannedPublicationUtc = 120000L,
                actualPublicationUtc = 120000L,
                publicationStatus = PublicationStatus.PUBLISHED_CONFIRMED
            )
        )

        // Post 2: Confirmed YT Shorts
        fakePublicationDao.insertPublication(
            ContentPublicationEntity(
                id = "p2",
                contentItemId = "c2",
                destination = PlatformType.YOUTUBE_SHORTS,
                plannedPublicationUtc = 130000L,
                actualPublicationUtc = 130000L,
                publicationStatus = PublicationStatus.PUBLISHED_CONFIRMED
            )
        )

        // Post 3: Scheduled / Reminder Sent TikTok (NOT confirmed yet!)
        fakePublicationDao.insertPublication(
            ContentPublicationEntity(
                id = "p3",
                contentItemId = "c3",
                destination = PlatformType.TIKTOK,
                plannedPublicationUtc = 140000L,
                publicationStatus = PublicationStatus.REMINDER_SENT
            )
        )

        val summary = publishingRepository.getWeeklyPublishingSummary(weekStart, weekEnd)

        assertEquals("Total weekly target must equal 14", 14, summary.totalTarget)
        assertEquals("Only confirmed posts count towards weekly progress", 2, summary.totalConfirmed)
        assertEquals(12, summary.totalRemaining)

        val shortsBreakdown = summary.platformBreakdown.first { it.platform == PlatformType.YOUTUBE_SHORTS }
        assertEquals(2, shortsBreakdown.confirmedPublished)
        assertEquals(3, shortsBreakdown.remaining)

        val tiktokBreakdown = summary.platformBreakdown.first { it.platform == PlatformType.TIKTOK }
        assertEquals("Reminder sent post must not be counted as published", 0, tiktokBreakdown.confirmedPublished)
        assertEquals(5, tiktokBreakdown.remaining)
    }
}

private class FakePublicationDao : PublicationDao {
    val pubs = mutableMapOf<String, ContentPublicationEntity>()

    override fun getAllPublications(): Flow<List<ContentPublicationEntity>> = flowOf(pubs.values.toList())
    override fun getPublicationsForContent(contentItemId: String): Flow<List<ContentPublicationEntity>> =
        flowOf(pubs.values.filter { it.contentItemId == contentItemId })
    override suspend fun getPublicationById(id: String): ContentPublicationEntity? = pubs[id]
    override fun observePublicationById(id: String): Flow<ContentPublicationEntity?> = flowOf(pubs[id])
    override fun getPublicationsScheduledForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentPublicationEntity>> =
        flowOf(pubs.values.filter { it.plannedPublicationUtc in startOfDayUtc..endOfDayUtc })
    override fun getPublicationsAwaitingConfirmation(): Flow<List<ContentPublicationEntity>> =
        flowOf(pubs.values.filter { it.publicationStatus == PublicationStatus.PUBLISHING_IN_PROGRESS || it.publicationStatus == PublicationStatus.REMINDER_SENT })
    override suspend fun getConfirmedPublicationsForDestination(destination: PlatformType, startUtc: Long, endUtc: Long): List<ContentPublicationEntity> =
        pubs.values.filter {
            it.destination == destination &&
            it.publicationStatus == PublicationStatus.PUBLISHED_CONFIRMED &&
            (it.actualPublicationUtc ?: 0L) in startUtc..endUtc
        }
    override fun observeConfirmedPublicationsBetween(startUtc: Long, endUtc: Long): Flow<List<ContentPublicationEntity>> =
        flowOf(pubs.values.filter { it.publicationStatus == PublicationStatus.PUBLISHED_CONFIRMED && (it.actualPublicationUtc ?: 0L) in startUtc..endUtc })
    override suspend fun insertPublication(publication: ContentPublicationEntity) { pubs[publication.id] = publication }
    override suspend fun insertPublications(publications: List<ContentPublicationEntity>) { publications.forEach { pubs[it.id] = it } }
    override suspend fun updatePublication(publication: ContentPublicationEntity) { pubs[publication.id] = publication }
    override suspend fun confirmPublication(id: String, actualTimeUtc: Long, url: String, status: PublicationStatus) {
        pubs[id]?.let {
            pubs[id] = it.copy(
                actualPublicationUtc = actualTimeUtc,
                publishedUrl = url,
                publicationStatus = status
            )
        }
    }
    override suspend fun deletePublication(id: String) { pubs.remove(id) }
}

private class FakeWorkflowChecklistDao : WorkflowAndChecklistDao {
    override suspend fun recordTransition(transition: WorkflowTransitionEntity) {}
    override fun getTransitionsForContent(contentItemId: String): Flow<List<WorkflowTransitionEntity>> = flowOf(emptyList())
    override suspend fun getChecklistForTarget(targetId: String): ChecklistEntity? = null
    override fun observeChecklistForTarget(targetId: String): Flow<ChecklistEntity?> = flowOf(null)
    override suspend fun insertChecklist(checklist: ChecklistEntity) {}
    override suspend fun updateChecklist(checklist: ChecklistEntity) {}
    override fun getChecklistItems(checklistId: String): Flow<List<ChecklistItemEntity>> = flowOf(emptyList())
    override suspend fun getChecklistItemsList(checklistId: String): List<ChecklistItemEntity> = emptyList()
    override suspend fun insertChecklistItems(items: List<ChecklistItemEntity>) {}
    override suspend fun updateChecklistItem(item: ChecklistItemEntity) {}
    override suspend fun setItemChecked(itemId: String, isChecked: Boolean) {}
}

private class FakePlatformAccountDao : PlatformAccountDao {
    val accounts = mutableListOf<PlatformAccountEntity>()

    override fun getAllAccounts(): Flow<List<PlatformAccountEntity>> = flowOf(accounts)
    override suspend fun getAllAccountsList(): List<PlatformAccountEntity> = accounts
    override suspend fun getAccountForPlatform(platform: PlatformType): PlatformAccountEntity? = accounts.find { it.platform == platform }
    override fun observeAccountForPlatform(platform: PlatformType): Flow<PlatformAccountEntity?> = flowOf(accounts.find { it.platform == platform })
    override suspend fun insertAccount(account: PlatformAccountEntity) { accounts.add(account) }
    override suspend fun insertAccounts(accountsList: List<PlatformAccountEntity>) { accounts.addAll(accountsList) }
    override suspend fun updateAccount(account: PlatformAccountEntity) {}
    override suspend fun updateAudienceCount(platform: PlatformType, count: Long, timestampUtc: Long) {}
    override suspend fun updateWeeklyTarget(platform: PlatformType, target: Int) {}
}

private class FakeTaskDao : TaskDao {
    val tasks = mutableMapOf<String, ContentTaskEntity>()

    override fun getAllTasks(): Flow<List<ContentTaskEntity>> = flowOf(tasks.values.toList())
    override suspend fun getTaskById(id: String): ContentTaskEntity? = tasks[id]
    override fun observeTaskById(id: String): Flow<ContentTaskEntity?> = flowOf(tasks[id])
    override fun getTasksForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentTaskEntity>> = flowOf(emptyList())
    override suspend fun getTasksForDayList(startOfDayUtc: Long, endOfDayUtc: Long): List<ContentTaskEntity> = emptyList()
    override fun getCriticalMissedTasks(): Flow<List<ContentTaskEntity>> = flowOf(emptyList())
    override suspend fun getCriticalMissedTasksList(): List<ContentTaskEntity> = emptyList()
    override fun getOverdueTasks(nowUtc: Long): Flow<List<ContentTaskEntity>> = flowOf(emptyList())
    override suspend fun getOverdueTasksList(nowUtc: Long): List<ContentTaskEntity> = emptyList()
    override fun observeActiveTask(): Flow<ContentTaskEntity?> = flowOf(null)
    override suspend fun getActiveTask(): ContentTaskEntity? = null
    override fun observeNextUpcomingTask(nowUtc: Long, endOfDayUtc: Long): Flow<ContentTaskEntity?> = flowOf(null)
    override suspend fun insertTask(task: ContentTaskEntity) { tasks[task.id] = task }
    override suspend fun insertTasks(tasksList: List<ContentTaskEntity>) { tasksList.forEach { tasks[it.id] = it } }
    override suspend fun updateTask(task: ContentTaskEntity) { tasks[task.id] = task }
    override suspend fun updateTaskStatus(id: String, status: TaskStatus) {
        tasks[id]?.let { tasks[id] = it.copy(status = status) }
    }
    override suspend fun completeTask(id: String, completionTimeUtc: Long, actualDuration: Int) {
        tasks[id]?.let { tasks[id] = it.copy(status = TaskStatus.COMPLETED, completionTimestampUtc = completionTimeUtc, actualDurationMinutes = actualDuration) }
    }
    override suspend fun skipTask(id: String, reason: String) {
        tasks[id]?.let { tasks[id] = it.copy(status = TaskStatus.SKIPPED, skipReason = reason) }
    }
    override suspend fun blockTask(id: String, reason: String) {
        tasks[id]?.let { tasks[id] = it.copy(status = TaskStatus.BLOCKED, isBlocked = true, blockerReason = reason) }
    }
    override suspend fun resolveBlocker(id: String) {
        tasks[id]?.let { tasks[id] = it.copy(status = TaskStatus.UPCOMING, isBlocked = false, blockerReason = null) }
    }
    override suspend fun rescheduleTask(id: String, newStartUtc: Long, newEndUtc: Long, reason: String) {
        tasks[id]?.let { tasks[id] = it.copy(scheduledStartTimeUtc = newStartUtc, scheduledEndTimeUtc = newEndUtc, status = TaskStatus.UPCOMING) }
    }
    override suspend fun recordEmergencyOverride(id: String) {
        tasks[id]?.let { tasks[id] = it.copy(emergencyOverrideRecorded = true, status = TaskStatus.UPCOMING) }
    }
    override suspend fun deleteTask(id: String) { tasks.remove(id) }
}
