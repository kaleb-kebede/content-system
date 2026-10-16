package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "content_derivatives",
    foreignKeys = [
        ForeignKey(
            entity = ContentItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["masterContentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["masterContentId"]),
        Index(value = ["destinationPlatform"])
    ]
)
data class ContentDerivativeEntity(
    @PrimaryKey
    val id: String,
    val masterContentId: String,
    val title: String,
    val destinationPlatform: PlatformType,
    val specificHook: String = "",
    val scriptSnippet: String = "",
    val caption: String = "",
    val hashtags: List<String> = emptyList(),
    val editingNotes: String = "",
    val mediaReferenceUri: String? = null,
    val plannedPublicationUtc: Long? = null,
    val publicationId: String? = null,
    val createdAtUtc: Long = System.currentTimeMillis()
)
