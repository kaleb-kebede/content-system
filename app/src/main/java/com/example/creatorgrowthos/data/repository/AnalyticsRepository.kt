package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.AnalyticsSnapshotDao
import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class PlatformPerformanceOverview(
    val platform: PlatformType,
    val totalSnapshots: Int,
    val totalViews: Long,
    val medianViews: Long,
    val averageEngagementRate: Float,
    val totalFollowersOrSubsGained: Int
)

class AnalyticsRepository(
    private val analyticsDao: AnalyticsSnapshotDao
) {
    fun getAllSnapshots(): Flow<List<AnalyticsSnapshotEntity>> = analyticsDao.getAllRecentSnapshots()

    fun getSnapshotsForPlatform(platform: PlatformType): Flow<List<AnalyticsSnapshotEntity>> =
        analyticsDao.getSnapshotsForPlatform(platform)

    fun getSnapshotsForPublication(publicationId: String): Flow<List<AnalyticsSnapshotEntity>> =
        analyticsDao.getSnapshotsForPublication(publicationId)

    suspend fun recordManualSnapshot(snapshot: AnalyticsSnapshotEntity): AnalyticsSnapshotEntity {
        // Calculate engagement rate
        val engagementRate = calculateEngagementRate(snapshot)
        val ready = snapshot.copy(
            id = if (snapshot.id.isBlank()) UUID.randomUUID().toString() else snapshot.id,
            calculatedEngagementRate = engagementRate,
            isManualEntry = true,
            dataSource = "MANUAL_INPUT",
            snapshotDateUtc = if (snapshot.snapshotDateUtc == 0L) System.currentTimeMillis() else snapshot.snapshotDateUtc
        )
        analyticsDao.insertSnapshot(ready)
        return ready
    }

    suspend fun getPerformanceOverviewForPlatform(platform: PlatformType): PlatformPerformanceOverview {
        val snapshots = analyticsDao.getSnapshotsForPlatformList(platform)
        if (snapshots.isEmpty()) {
            return PlatformPerformanceOverview(
                platform = platform,
                totalSnapshots = 0,
                totalViews = 0,
                medianViews = 0,
                averageEngagementRate = 0f,
                totalFollowersOrSubsGained = 0
            )
        }

        val viewsList = snapshots.mapNotNull { it.views }.sorted()
        val totalViews = viewsList.sum()
        val medianViews = if (viewsList.isNotEmpty()) {
            viewsList[viewsList.size / 2]
        } else 0L

        val engagements = snapshots.mapNotNull { it.calculatedEngagementRate }
        val avgEngagement = if (engagements.isNotEmpty()) engagements.average().toFloat() else 0f

        val gains = when (platform) {
            PlatformType.YOUTUBE_LONG, PlatformType.YOUTUBE_SHORTS -> snapshots.mapNotNull { it.subscribersGained }.sum()
            else -> snapshots.mapNotNull { it.followersGained }.sum()
        }

        return PlatformPerformanceOverview(
            platform = platform,
            totalSnapshots = snapshots.size,
            totalViews = totalViews,
            medianViews = medianViews,
            averageEngagementRate = avgEngagement,
            totalFollowersOrSubsGained = gains
        )
    }

    private fun calculateEngagementRate(snapshot: AnalyticsSnapshotEntity): Float? {
        val views = snapshot.views ?: return null
        if (views <= 0) return 0f

        return when (snapshot.platform) {
            PlatformType.YOUTUBE_LONG, PlatformType.YOUTUBE_SHORTS -> {
                val interactions = (snapshot.likes ?: 0) + (snapshot.comments ?: 0)
                (interactions.toFloat() / views * 100f)
            }
            PlatformType.TIKTOK, PlatformType.INSTAGRAM_REELS -> {
                val interactions = (snapshot.likes ?: 0) + (snapshot.comments ?: 0) +
                        (snapshot.shares ?: 0) + (snapshot.saves ?: 0)
                (interactions.toFloat() / views * 100f)
            }
        }
    }
}
