package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "content_publications",
    foreignKeys = [
        ForeignKey(
            entity = ContentItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["contentItemId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["contentItemId"]),
        Index(value = ["destination"]),
        Index(value = ["plannedPublicationUtc"]),
        Index(value = ["publicationStatus"]),
        Index(value = ["actualPublicationUtc"])
    ]
)
data class ContentPublicationEntity(
    @PrimaryKey
    val id: String,
    val contentItemId: String,
    val derivativeId: String? = null,
    val destination: PlatformType,
    val format: ContentFormat = ContentFormat.SHORT_FORM_VIDEO,
    val platformSpecificTitle: String = "",
    val caption: String = "",
    val hashtags: List<String> = emptyList(),
    val mediaReferenceUri: String? = null,
    val plannedPublicationUtc: Long,
    val timeZoneId: String = "UTC",
    val publicationStatus: PublicationStatus = PublicationStatus.DRAFT,
    val actualPublicationUtc: Long? = null,
    val publishedUrl: String? = null,
    val notes: String = "",
    val reminderTriggeredUtc: Long? = null,
    val checklistCompleted: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis()
)
