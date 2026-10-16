package com.example.creatorgrowthos

import com.example.creatorgrowthos.data.local.dao.ContentItemDao
import com.example.creatorgrowthos.data.local.dao.ScriptDao
import com.example.creatorgrowthos.data.local.dao.WorkflowAndChecklistDao
import com.example.creatorgrowthos.data.local.entity.ChecklistEntity
import com.example.creatorgrowthos.data.local.entity.ChecklistItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.local.entity.ScriptEntity
import com.example.creatorgrowthos.data.local.entity.ScriptSectionEntity
import com.example.creatorgrowthos.data.local.entity.WorkflowTransitionEntity
import com.example.creatorgrowthos.data.repository.ContentRepository
import com.example.creatorgrowthos.data.repository.StageTransitionResult
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ContentPipelineTest {

    private lateinit var contentRepository: ContentRepository
    private val fakeItemDao = FakeContentItemDaoPipeline()
    private val fakeScriptDao = FakeScriptDaoPipeline()
    private val fakeWorkflowDao = FakeWorkflowDaoPipeline()

    @Before
    fun setup() {
        contentRepository = ContentRepository(fakeItemDao, fakeScriptDao, fakeWorkflowDao)
    }

    @Test
    fun transitionStage_validProgression_succeedsAndRecordsTransition() = runTest {
        val item = ContentItemEntity(
            id = "c1",
            title = "Master Productivity Systems",
            status = WorkflowStage.IDEA
        )
        fakeItemDao.insertContentItem(item)

        val result = contentRepository.transitionStage("c1", WorkflowStage.VALIDATION)
        assertTrue(result is StageTransitionResult.Success)

        val updated = fakeItemDao.getContentItemById("c1")
        assertEquals(WorkflowStage.VALIDATION, updated?.status)
        assertEquals(1, fakeWorkflowDao.recordedTransitions.size)
        assertEquals(WorkflowStage.IDEA, fakeWorkflowDao.recordedTransitions[0].fromStage)
        assertEquals(WorkflowStage.VALIDATION, fakeWorkflowDao.recordedTransitions[0].toStage)
    }

    @Test
    fun transitionStage_toApprovedWithoutThumbnail_failsValidation() = runTest {
        val item = ContentItemEntity(
            id = "c2",
            title = "Viral Algorithm Deep Dive",
            status = WorkflowStage.QUALITY_REVIEW,
            thumbnailIdea = "", // Missing thumbnail
            targetPlatforms = listOf("YOUTUBE_LONG")
        )
        fakeItemDao.insertContentItem(item)

        val result = contentRepository.transitionStage("c2", WorkflowStage.APPROVED)
        assertTrue("Must reject approval without thumbnail concept", result is StageTransitionResult.ValidationFailed)

        val unchanged = fakeItemDao.getContentItemById("c2")
        assertEquals(WorkflowStage.QUALITY_REVIEW, unchanged?.status)
    }

    @Test
    fun transitionStage_toApprovedWithoutTargetPlatforms_failsValidation() = runTest {
        val item = ContentItemEntity(
            id = "c3",
            title = "Monetization Blueprint",
            status = WorkflowStage.QUALITY_REVIEW,
            thumbnailIdea = "Bold 3D Money Graphic with glowing text",
            targetPlatforms = emptyList() // Missing platforms
        )
        fakeItemDao.insertContentItem(item)

        val result = contentRepository.transitionStage("c3", WorkflowStage.APPROVED)
        assertTrue("Must reject approval without platforms selected", result is StageTransitionResult.ValidationFailed)
    }

    @Test
    fun transitionStage_toScheduledWithoutBeingApprovedFirst_failsValidation() = runTest {
        val item = ContentItemEntity(
            id = "c4",
            title = "Script Breakdown",
            status = WorkflowStage.SCRIPTING,
            publicationDeadlineUtc = System.currentTimeMillis() + 86400000L
        )
        fakeItemDao.insertContentItem(item)

        val result = contentRepository.transitionStage("c4", WorkflowStage.SCHEDULED)
        assertTrue("Cannot jump directly to SCHEDULED without being APPROVED", result is StageTransitionResult.ValidationFailed)
    }

    @Test
    fun transitionStage_toScheduledWhenApprovedWithDeadline_succeeds() = runTest {
        val item = ContentItemEntity(
            id = "c5",
            title = "Camera Gear Guide",
            status = WorkflowStage.APPROVED,
            thumbnailIdea = "Sony FX3 floating in neon lighting",
            targetPlatforms = listOf("YOUTUBE_LONG", "TIKTOK"),
            publicationDeadlineUtc = System.currentTimeMillis() + 86400000L
        )
        fakeItemDao.insertContentItem(item)

        val result = contentRepository.transitionStage("c5", WorkflowStage.SCHEDULED)
        assertTrue("Must allow transition to SCHEDULED when APPROVED and deadline set", result is StageTransitionResult.Success)

        val scheduled = fakeItemDao.getContentItemById("c5")
        assertEquals(WorkflowStage.SCHEDULED, scheduled?.status)
    }
}

private class FakeContentItemDaoPipeline : ContentItemDao {
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

private class FakeScriptDaoPipeline : ScriptDao {
    val scripts = mutableMapOf<String, ScriptEntity>()

    override fun getAllScripts(): Flow<List<ScriptEntity>> = flowOf(scripts.values.toList())
    override suspend fun getScriptById(id: String): ScriptEntity? = scripts[id]
    override fun observeScriptById(id: String): Flow<ScriptEntity?> = flowOf(scripts[id])
    override suspend fun getScriptForContentItem(contentItemId: String): ScriptEntity? = scripts.values.find { it.contentItemId == contentItemId }
    override suspend fun insertScript(script: ScriptEntity) { scripts[script.id] = script }
    override suspend fun updateScript(script: ScriptEntity) { scripts[script.id] = script }
    override suspend fun deleteScript(id: String) { scripts.remove(id) }
    override fun getSectionsForScript(scriptId: String): Flow<List<ScriptSectionEntity>> = flowOf(emptyList())
    override suspend fun insertSections(sections: List<ScriptSectionEntity>) {}
    override suspend fun deleteSectionsForScript(scriptId: String) {}
}

private class FakeWorkflowDaoPipeline : WorkflowAndChecklistDao {
    val recordedTransitions = mutableListOf<WorkflowTransitionEntity>()

    override suspend fun recordTransition(transition: WorkflowTransitionEntity) {
        recordedTransitions.add(transition)
    }
    override fun getTransitionsForContent(contentItemId: String): Flow<List<WorkflowTransitionEntity>> =
        flowOf(recordedTransitions.filter { it.contentItemId == contentItemId })
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
