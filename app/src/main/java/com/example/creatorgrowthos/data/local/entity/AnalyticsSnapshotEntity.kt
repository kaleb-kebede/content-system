package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "analytics_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = ContentPublicationEntity::class,
            parentColumns = ["id"],
            childColumns = ["publicationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["publicationId"]),
        Index(value = ["platform"]),
        Index(value = ["snapshotDateUtc"])
    ]
)
data class AnalyticsSnapshotEntity(
    @PrimaryKey
    val id: String,
    val publicationId: String,
    val platform: PlatformType,
    val snapshotDateUtc: Long = System.currentTimeMillis(),
    val measurementWindow: String = "7D", // 24H, 48H, 7D, 30D, LIFETIME
    val isManualEntry: Boolean = true,
    val dataSource: String = "MANUAL_INPUT",

    // YouTube Metrics
    val views: Long? = null,
    val impressions: Long? = null,
    val clickThroughRate: Float? = null, // e.g. 5.4%
    val watchTimeHours: Float? = null,
    val averageViewDurationSeconds: Int? = null,
    val averagePercentageViewed: Float? = null, // e.g. 62.5%
    val subscribersGained: Int? = null,

    // TikTok & Reels Common Metrics
    val likes: Long? = null,
    val comments: Long? = null,
    val shares: Long? = null,
    val saves: Long? = null,
    val reach: Long? = null,
    val completionRate: Float? = null, // e.g. 45.2%
    val averageWatchTimeSeconds: Float? = null,
    val followersGained: Int? = null,

    // Calculated fields
    val calculatedEngagementRate: Float? = null,
    val notes: String = ""
)
