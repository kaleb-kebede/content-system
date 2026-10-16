package com.example.creatorgrowthos

import com.example.creatorgrowthos.data.local.dao.ContentDerivativeDao
import com.example.creatorgrowthos.data.local.dao.ContentItemDao
import com.example.creatorgrowthos.data.local.dao.PublicationDao
import com.example.creatorgrowthos.data.local.entity.ContentDerivativeEntity
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.repository.RepurposingRepository
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RepurposingTest {

    private lateinit var repurposingRepository: RepurposingRepository
    private val fakeDerivativeDao = FakeDerivativeDao()
    private val fakeItemDao = FakeContentItemDaoRepurposing()
    private val fakePubDao = FakePublicationDaoRepurposing()

    @Before
    fun setup() {
        repurposingRepository = RepurposingRepository(
            fakeDerivativeDao,
            fakeItemDao,
            fakePubDao
        )
    }

    @Test
    fun createDerivative_createsIndependentDerivativeAndPublicationDraft() = runTest {
        val masterId = "master_yt_1"
        fakeItemDao.insertContentItem(
            ContentItemEntity(
                id = masterId,
                title = "Ultimate Productivity Framework (Long-form)",
                status = WorkflowStage.PUBLISHED
            )
        )

        val plannedTime = System.currentTimeMillis() + 86400000L

        // Repurpose into Shorts clip
        val shortsDerivative = repurposingRepository.createDerivative(
            masterContentId = masterId,
            destination = PlatformType.YOUTUBE_SHORTS,
            title = "Stop Working 12 Hours: The 3-Block System",
            hook = "You're not lazy, your schedule is broken.",
            caption = "How I cut 4 hours from my day using time blocking.",
            hashtags = listOf("#productivity", "#shorts", "#creators"),
            editingNotes = "Crop to 9:16, add kinetic captions, zoom in at 0:15",
            plannedPublicationUtc = plannedTime
        )

        // Repurpose into TikTok clip
        val tiktokDerivative = repurposingRepository.createDerivative(
            masterContentId = masterId,
            destination = PlatformType.TIKTOK,
            title = "Why your morning routine is failing",
            hook = "The 7 AM myth nobody talks about.",
            caption = "Check out the full breakdown on YouTube!",
            hashtags = listOf("#tiktokgrowth", "#mindset"),
            editingNotes = "Use trending sound in background",
            plannedPublicationUtc = plannedTime + 3600000L
        )

        assertEquals(2, fakeDerivativeDao.derivatives.size)
        assertEquals(masterId, shortsDerivative.masterContentId)
        assertEquals(masterId, tiktokDerivative.masterContentId)

        assertNotEquals("Each derivative must have its own unique ID", shortsDerivative.id, tiktokDerivative.id)
        assertNotEquals("Each derivative must have its own hook", shortsDerivative.specificHook, tiktokDerivative.specificHook)

        // Check corresponding publication drafts
        assertEquals(2, fakePubDao.publications.size)
        val shortsPub = fakePubDao.publications.find { it.destination == PlatformType.YOUTUBE_SHORTS }
        assertNotNull(shortsPub)
        assertEquals(shortsDerivative.id, shortsPub?.derivativeId)
        assertEquals(masterId, shortsPub?.contentItemId)
        assertEquals(PublicationStatus.DRAFT, shortsPub?.publicationStatus)
    }
}

private class FakeContentItemDaoRepurposing : ContentItemDao {
    val items = mutableMapOf<String, ContentItemEntity>()

    override fun getAllContentItems(): Flow<List<ContentItemEntity>> = flowOf(items.values.toList())
    override fun getActivePipelineItems(): Flow<List<ContentItemEntity>> =
        flowOf(items.values.filter { !it.status.isTerminal() })
    override fun getContentItemsByStage(stage: WorkflowStage): Flow<List<ContentItemEntity>> =
        flowOf(items.values.filter { it.status == stage })
    override fun getContentItemsWithDeadlineBetween(startUtc: Long, endUtc: Long): Flow<List<ContentItemEntity>> =
        flowOf(items.values.filter { (it.publicationDeadlineUtc ?: 0L) in startUtc..endUtc })
    override suspend fun countByStage(stage: WorkflowStage): Int = items.values.count { it.status == stage }
    override suspend fun getContentItemById(id: String): ContentItemEntity? = items[id]
    override fun observeContentItemById(id: String): Flow<ContentItemEntity?> = flowOf(items[id])
    override suspend fun insertContentItem(item: ContentItemEntity) { items[item.id] = item }
    override suspend fun insertContentItems(itemsList: List<ContentItemEntity>) { itemsList.forEach { items[it.id] = it } }
    override suspend fun updateContentItem(item: ContentItemEntity) { items[item.id] = item }
    override suspend fun updateStage(id: String, stage: WorkflowStage, timestampUtc: Long) {
        items[id]?.let { items[id] = it.copy(status = stage, updatedAtUtc = timestampUtc) }
    }
    override suspend fun deleteContentItem(id: String) { items.remove(id) }
}

private class FakeDerivativeDao : ContentDerivativeDao {
    val derivatives = mutableMapOf<String, ContentDerivativeEntity>()

    override fun getAllDerivatives(): Flow<List<ContentDerivativeEntity>> = flowOf(derivatives.values.toList())
    override fun getDerivativesForMaster(masterContentId: String): Flow<List<ContentDerivativeEntity>> =
        flowOf(derivatives.values.filter { it.masterContentId == masterContentId })
    override suspend fun getDerivativeById(id: String): ContentDerivativeEntity? = derivatives[id]
    override suspend fun insertDerivative(derivative: ContentDerivativeEntity) { derivatives[derivative.id] = derivative }
    override suspend fun insertDerivatives(derivativesList: List<ContentDerivativeEntity>) { derivativesList.forEach { derivatives[it.id] = it } }
    override suspend fun updateDerivative(derivative: ContentDerivativeEntity) { derivatives[derivative.id] = derivative }
    override suspend fun deleteDerivative(id: String) { derivatives.remove(id) }
}

private class FakePublicationDaoRepurposing : PublicationDao {
    val publications = mutableListOf<ContentPublicationEntity>()

    override fun getAllPublications(): Flow<List<ContentPublicationEntity>> = flowOf(publications)
    override fun getPublicationsForContent(contentItemId: String): Flow<List<ContentPublicationEntity>> =
        flowOf(publications.filter { it.contentItemId == contentItemId })
    override suspend fun getPublicationById(id: String): ContentPublicationEntity? = publications.find { it.id == id }
    override fun observePublicationById(id: String): Flow<ContentPublicationEntity?> = flowOf(publications.find { it.id == id })
    override fun getPublicationsScheduledForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentPublicationEntity>> = flowOf(emptyList())
    override fun getPublicationsAwaitingConfirmation(): Flow<List<ContentPublicationEntity>> = flowOf(emptyList())
    override suspend fun getConfirmedPublicationsForDestination(destination: PlatformType, startUtc: Long, endUtc: Long): List<ContentPublicationEntity> = emptyList()
    override fun observeConfirmedPublicationsBetween(startUtc: Long, endUtc: Long): Flow<List<ContentPublicationEntity>> = flowOf(emptyList())
    override suspend fun insertPublication(publication: ContentPublicationEntity) { publications.add(publication) }
    override suspend fun insertPublications(publicationsList: List<ContentPublicationEntity>) { publications.addAll(publicationsList) }
    override suspend fun updatePublication(publication: ContentPublicationEntity) {}
    override suspend fun confirmPublication(id: String, actualTimeUtc: Long, url: String, status: PublicationStatus) {}
    override suspend fun deletePublication(id: String) { publications.removeAll { it.id == id } }
}
