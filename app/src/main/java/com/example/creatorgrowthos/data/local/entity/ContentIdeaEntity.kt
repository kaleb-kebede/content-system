package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "content_ideas",
    indices = [
        Index(value = ["status"]),
        Index(value = ["demandScore"]),
        Index(value = ["captureDateUtc"])
    ]
)
data class ContentIdeaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val rawIdea: String = "",
    val targetAudience: String = "",
    val audienceProblem: String = "",
    val contentPillarId: String? = null,
    val format: ContentFormat = ContentFormat.SHORT_FORM_VIDEO,
    val suggestedPlatform: PlatformType? = null,
    val sourceUrl: String? = null,
    val demandScore: Int = 5,
    val demandExplanation: String = "Balanced initial estimate",
    val strategicFit: Int = 5,
    val productionEffort: Int = 3,
    val status: String = "RAW", // RAW, VALIDATED, PROMOTED, ARCHIVED
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val captureDateUtc: Long = System.currentTimeMillis(),
    val convertedContentItemId: String? = null
)
