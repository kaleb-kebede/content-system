package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.AppDatabase
import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.data.local.entity.ScriptEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class CreatorBackupPayload(
    val schemaVersion: Int = 1,
    val backupTimestampUtc: Long = System.currentTimeMillis(),
    val ideas: List<ContentIdeaEntity> = emptyList(),
    val contentItems: List<ContentItemEntity> = emptyList(),
    val scripts: List<ScriptEntity> = emptyList(),
    val publications: List<ContentPublicationEntity> = emptyList(),
    val tasks: List<ContentTaskEntity> = emptyList(),
    val platformAccounts: List<PlatformAccountEntity> = emptyList(),
    val analyticsSnapshots: List<AnalyticsSnapshotEntity> = emptyList()
)

data class RestoreValidationResult(
    val isValid: Boolean,
    val schemaVersion: Int,
    val itemCountSummary: String,
    val errorMessage: String? = null
)

class BackupRestoreRepository(
    private val database: AppDatabase
) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    /**
     * Exports entire database content into a validated JSON string.
     */
    suspend fun createJsonBackup(): String = withContext(Dispatchers.IO) {
        val ideas = database.contentIdeaDao().getAllIdeas().first()
        val items = database.contentItemDao().getAllContentItems().first()
        val scripts = database.scriptDao().getAllScripts().first()
        val publications = database.publicationDao().getAllPublications().first()
        val tasks = database.taskDao().getAllTasks().first()
        val accounts = database.platformAccountDao().getAllAccountsList()
        val snapshots = database.analyticsSnapshotDao().getAllSnapshotsList()

        val payload = CreatorBackupPayload(
            schemaVersion = 1,
            backupTimestampUtc = System.currentTimeMillis(),
            ideas = ideas,
            contentItems = items,
            scripts = scripts,
            publications = publications,
            tasks = tasks,
            platformAccounts = accounts,
            analyticsSnapshots = snapshots
        )

        json.encodeToString(payload)
    }

    /**
     * Validates a backup JSON payload prior to restoring.
     */
    fun validateBackup(backupJson: String): RestoreValidationResult {
        return try {
            val payload = json.decodeFromString<CreatorBackupPayload>(backupJson)
            if (payload.schemaVersion < 1) {
                RestoreValidationResult(false, payload.schemaVersion, "", "Invalid schema version")
            } else {
                val summary = "${payload.ideas.size} Ideas, ${payload.contentItems.size} Content Items, " +
                        "${payload.scripts.size} Scripts, ${payload.publications.size} Posts, " +
                        "${payload.tasks.size} Tasks, ${payload.analyticsSnapshots.size} Snapshots"
                RestoreValidationResult(true, payload.schemaVersion, summary, null)
            }
        } catch (e: Exception) {
            RestoreValidationResult(false, 0, "", "Parsing error: ${e.localizedMessage}")
        }
    }

    /**
     * Restores database content from validated JSON payload.
     */
    suspend fun restoreFromJson(backupJson: String, replaceExisting: Boolean = false): Boolean = withContext(Dispatchers.IO) {
        try {
            val payload = json.decodeFromString<CreatorBackupPayload>(backupJson)
            if (payload.schemaVersion < 1) return@withContext false

            if (replaceExisting) {
                database.clearAllTables()
            }

            database.contentIdeaDao().insertIdeas(payload.ideas)
            database.contentItemDao().insertContentItems(payload.contentItems)
            for (script in payload.scripts) {
                database.scriptDao().insertScript(script)
            }
            database.publicationDao().insertPublications(payload.publications)
            database.taskDao().insertTasks(payload.tasks)
            database.platformAccountDao().insertAccounts(payload.platformAccounts)
            database.analyticsSnapshotDao().insertSnapshots(payload.analyticsSnapshots)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Generates a CSV export of all recorded analytics snapshots.
     */
    suspend fun exportAnalyticsToCsv(): String = withContext(Dispatchers.IO) {
        val snapshots = database.analyticsSnapshotDao().getAllSnapshotsList()
        val sb = StringBuilder()
        sb.append("id,platform,publicationId,dateUtc,window,views,impressions,ctr,likes,comments,shares,saves,engagementRate,subsOrFollowersGained\n")

        for (s in snapshots) {
            sb.append("\"${s.id}\",")
            sb.append("\"${s.platform.displayName}\",")
            sb.append("\"${s.publicationId}\",")
            sb.append("${s.snapshotDateUtc},")
            sb.append("\"${s.measurementWindow}\",")
            sb.append("${s.views ?: ""},")
            sb.append("${s.impressions ?: ""},")
            sb.append("${s.clickThroughRate ?: ""},")
            sb.append("${s.likes ?: ""},")
            sb.append("${s.comments ?: ""},")
            sb.append("${s.shares ?: ""},")
            sb.append("${s.saves ?: ""},")
            sb.append("${s.calculatedEngagementRate ?: ""},")
            val gains = s.subscribersGained ?: s.followersGained ?: ""
            sb.append("$gains\n")
        }

        sb.toString()
    }
}
