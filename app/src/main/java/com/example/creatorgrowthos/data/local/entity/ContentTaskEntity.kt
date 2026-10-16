package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "content_tasks",
    foreignKeys = [
        ForeignKey(
            entity = ContentItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["linkedContentItemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["status"]),
        Index(value = ["isCritical"]),
        Index(value = ["scheduledStartTimeUtc"]),
        Index(value = ["scheduledEndTimeUtc"]),
        Index(value = ["deadlineUtc"]),
        Index(value = ["linkedContentItemId"])
    ]
)
data class ContentTaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String = "",
    val linkedContentItemId: String? = null,
    val workflowStage: WorkflowStage? = null,
    val scheduledStartTimeUtc: Long,
    val scheduledEndTimeUtc: Long,
    val deadlineUtc: Long? = null,
    val estimatedDurationMinutes: Int = 30,
    val actualDurationMinutes: Int = 0,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCritical: Boolean = false,
    val dependencyTaskIds: List<String> = emptyList(),
    val status: TaskStatus = TaskStatus.UPCOMING,
    val reminderMinutesBefore: Int = 10,
    val reminderConfigured: Boolean = true,
    val completionTimestampUtc: Long? = null,
    val skipReason: String? = null,
    val rescheduleReason: String? = null,
    val originalPlannedDateUtc: Long = scheduledStartTimeUtc,
    val rescheduleCount: Int = 0,
    val isBlocked: Boolean = false,
    val blockerReason: String? = null,
    val emergencyOverrideRecorded: Boolean = false,
    val createdAtUtc: Long = System.currentTimeMillis()
)
