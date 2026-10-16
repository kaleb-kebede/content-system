package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.ContentFormat
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "scripts",
    foreignKeys = [
        ForeignKey(
            entity = ContentItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["contentItemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["contentItemId"])
    ]
)
data class ScriptEntity(
    @PrimaryKey
    val id: String,
    val contentItemId: String? = null,
    val title: String,
    val format: ContentFormat = ContentFormat.SHORT_FORM_VIDEO,
    val contentPillarId: String? = null,
    val cta: String = "",
    val targetAudience: String = "",
    val hook: String = "",
    val outline: String = "",
    val scriptBody: String = "",
    val voiceOverNotes: String = "",
    val onScreenText: String = "",
    val visualNotes: String = "",
    val researchSources: String = "",
    val estimatedDurationSeconds: Int = 0,
    val wordCount: Int = 0,
    val status: String = "DRAFT", // DRAFT, REVIEW, APPROVED
    val narrationWordsPerMinute: Int = 140,
    val revisionNumber: Int = 1,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "script_sections",
    foreignKeys = [
        ForeignKey(
            entity = ScriptEntity::class,
            parentColumns = ["id"],
            childColumns = ["scriptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scriptId", "sectionOrder"])
    ]
)
data class ScriptSectionEntity(
    @PrimaryKey
    val id: String,
    val scriptId: String,
    val sectionType: String, // HOOK, CURIOSITY_GAP, EXPLANATION, PROGRESSION, PAYOFF, CTA, CONTEXT, MAIN_SECTION
    val sectionOrder: Int,
    val sceneNumber: Int = 1,
    val title: String = "",
    val spokenWords: String = "",
    val visualNotes: String = "",
    val onScreenText: String = "",
    val durationEstimateSeconds: Int = 0
)
