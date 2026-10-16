package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "platform_accounts",
    indices = [
        Index(value = ["platform"], unique = true)
    ]
)
data class PlatformAccountEntity(
    @PrimaryKey
    val id: String,
    val platform: PlatformType,
    val accountHandle: String,
    val currentAudienceCount: Long = 0,
    val targetAudienceCount: Long = 100000,
    val weeklyPublishingTarget: Int = 5,
    val lastUpdatedUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "content_pillars")
data class ContentPillarEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val colorHex: String = "#38BDF8",
    val targetPercentage: Float = 25f,
    val isActive: Boolean = true
)

@Serializable
@Entity(tableName = "content_tags")
data class ContentTagEntity(
    @PrimaryKey
    val id: String,
    val name: String
)

@Serializable
@Entity(
    tableName = "goals",
    indices = [
        Index(value = ["goalType"]),
        Index(value = ["isAchieved"])
    ]
)
data class GoalEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val goalType: String, // AUDIENCE_GROWTH, WEEKLY_OUTPUT, QUARTERLY_OBJECTIVE, MONETIZATION, EXPERIMENT
    val platform: PlatformType? = null,
    val currentMetric: Float = 0f,
    val targetMetric: Float = 100000f,
    val unit: String = "followers",
    val deadlineUtc: Long? = null,
    val isAchieved: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "reviews",
    indices = [
        Index(value = ["reviewType"]),
        Index(value = ["periodString"])
    ]
)
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val reviewType: String, // DAILY, WEEKLY, MONTHLY
    val periodString: String, // "YYYY-MM-DD" or "YYYY-Www"
    val timestampUtc: Long = System.currentTimeMillis(),
    val accomplishments: String = "",
    val bottlenecks: String = "",
    val learnings: String = "",
    val plannedAdjustments: String = "",
    val ratingScore: Int = 4
)
