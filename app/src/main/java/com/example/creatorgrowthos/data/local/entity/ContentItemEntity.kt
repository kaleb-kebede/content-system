package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "content_items",
    indices = [
        Index(value = ["status"]),
        Index(value = ["priority"]),
        Index(value = ["productionDeadlineUtc"]),
        Index(value = ["publicationDeadlineUtc"]),
        Index(value = ["originalIdeaId"])
    ]
)
data class ContentItemEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val contentType: ContentFormat = ContentFormat.SHORT_FORM_VIDEO,
    val contentPillarId: String? = null,
    val demandScore: Int = 5,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: WorkflowStage = WorkflowStage.PLANNED,
    val scriptId: String? = null,
    val thumbnailIdea: String = "",
    val thumbnailLocalPath: String? = null,
    val voiceOverTool: String = "Mic / In-Person",
    val productionDeadlineUtc: Long? = null,
    val publicationDeadlineUtc: Long? = null,
    val nextAction: String = "Complete research & outline",
    val targetPlatforms: List<String> = emptyList(),
    val notes: String = "",
    val originalIdeaId: String? = null,
    val isMaster: Boolean = true,
    val createdAtUtc: Long = System.currentTimeMillis(),
    val updatedAtUtc: Long = System.currentTimeMillis()
)
