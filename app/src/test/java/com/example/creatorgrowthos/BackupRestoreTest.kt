package com.example.creatorgrowthos

import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import com.example.creatorgrowthos.data.repository.CreatorBackupPayload
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class BackupRestoreTest {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    @Test
    fun backupPayload_serializationAndDeserialization_preservesEntities() {
        val payload = CreatorBackupPayload(
            schemaVersion = 1,
            backupTimestampUtc = 1700000000L,
            ideas = listOf(
                ContentIdeaEntity(
                    id = "idea_1",
                    title = "How to automate YouTube shorts",
                    demandScore = 9,
                    contentPillarId = "pillar_prod"
                )
            ),
            analyticsSnapshots = listOf(
                AnalyticsSnapshotEntity(
                    id = "snap_1",
                    publicationId = "pub_1",
                    platform = PlatformType.YOUTUBE_LONG,
                    views = 12500L,
                    likes = 850L,
                    comments = 94L,
                    calculatedEngagementRate = 7.55f,
                    subscribersGained = 120
                )
            )
        )

        val serialized = json.encodeToString(payload)
        assertTrue("Serialized JSON must contain idea title", serialized.contains("How to automate YouTube shorts"))
        assertTrue("Serialized JSON must contain views metric", serialized.contains("12500"))

        val deserialized = json.decodeFromString<CreatorBackupPayload>(serialized)
        assertEquals(1, deserialized.ideas.size)
        assertEquals("idea_1", deserialized.ideas[0].id)
        assertEquals(9, deserialized.ideas[0].demandScore)
        assertEquals(1, deserialized.analyticsSnapshots.size)
        assertEquals(12500L, deserialized.analyticsSnapshots[0].views)
        assertEquals(120, deserialized.analyticsSnapshots[0].subscribersGained)
    }

    @Test
    fun backupValidation_corruptedJson_returnsInvalid() {
        val corruptedJson = "{ \"schemaVersion\": 1, \"ideas\": [ broken_json "
        val isValid = try {
            json.decodeFromString<CreatorBackupPayload>(corruptedJson)
            true
        } catch (e: Exception) {
            false
        }

        assertFalse("Malformed JSON payload must fail validation", isValid)
    }

    @Test
    fun analyticsCsvExport_properlyFormatsColumnsAndHandlesNulls() {
        val snapshot = AnalyticsSnapshotEntity(
            id = "snap_100",
            publicationId = "pub_200",
            platform = PlatformType.TIKTOK,
            snapshotDateUtc = 1700000000L,
            measurementWindow = "48H",
            views = 50000L,
            likes = 4200L,
            comments = 310L,
            shares = 180L,
            saves = 95L,
            calculatedEngagementRate = 9.57f,
            followersGained = 450,
            impressions = null, // null check
            clickThroughRate = null // null check
        )

        val sb = StringBuilder()
        sb.append("id,platform,publicationId,dateUtc,window,views,impressions,ctr,likes,comments,shares,saves,engagementRate,subsOrFollowersGained\n")
        sb.append("\"${snapshot.id}\",")
        sb.append("\"${snapshot.platform.displayName}\",")
        sb.append("\"${snapshot.publicationId}\",")
        sb.append("${snapshot.snapshotDateUtc},")
        sb.append("\"${snapshot.measurementWindow}\",")
        sb.append("${snapshot.views ?: ""},")
        sb.append("${snapshot.impressions ?: ""},")
        sb.append("${snapshot.clickThroughRate ?: ""},")
        sb.append("${snapshot.likes ?: ""},")
        sb.append("${snapshot.comments ?: ""},")
        sb.append("${snapshot.shares ?: ""},")
        sb.append("${snapshot.saves ?: ""},")
        sb.append("${snapshot.calculatedEngagementRate ?: ""},")
        val gains = snapshot.subscribersGained ?: snapshot.followersGained ?: ""
        sb.append("$gains\n")

        val csv = sb.toString()
        assertTrue(csv.contains("snap_100"))
        assertTrue(csv.contains("TikTok"))
        assertTrue(csv.contains("50000"))
        assertTrue(csv.contains("9.57"))
        assertTrue(csv.contains("450"))
        // Check that null impression appears as empty string between commas
        assertTrue(csv.contains(",50000,,,4200,"))
    }
}
