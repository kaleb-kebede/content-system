package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "workflow_transitions",
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
        Index(value = ["timestampUtc"])
    ]
)
data class WorkflowTransitionEntity(
    @PrimaryKey
    val id: String,
    val contentItemId: String,
    val fromStage: WorkflowStage,
    val toStage: WorkflowStage,
    val timestampUtc: Long = System.currentTimeMillis(),
    val transitionReason: String = "",
    val validationChecksPassed: Boolean = true
)

@Serializable
@Entity(
    tableName = "checklists",
    indices = [
        Index(value = ["targetId"]),
        Index(value = ["targetType"])
    ]
)
data class ChecklistEntity(
    @PrimaryKey
    val id: String,
    val targetType: String, // PUBLICATION, QUALITY_REVIEW, PRE_RECORDING, WEEKLY_REVIEW
    val targetId: String,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = ChecklistEntity::class,
            parentColumns = ["id"],
            childColumns = ["checklistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["checklistId", "itemOrder"])
    ]
)
data class ChecklistItemEntity(
    @PrimaryKey
    val id: String,
    val checklistId: String,
    val text: String,
    val isChecked: Boolean = false,
    val isRequired: Boolean = true,
    val itemOrder: Int = 0
)
