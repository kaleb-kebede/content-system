package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "schedule_blocks",
    indices = [
        Index(value = ["startTimeUtc"]),
        Index(value = ["endTimeUtc"]),
        Index(value = ["taskId"])
    ]
)
data class ScheduleBlockEntity(
    @PrimaryKey
    val id: String,
    val blockType: String, // ROUTINE_TASK, TIME_BLOCK, BREAK, BUFFER, UNAVAILABLE
    val title: String,
    val startTimeUtc: Long,
    val endTimeUtc: Long,
    val isLocked: Boolean = false,
    val taskId: String? = null
)

@Serializable
@Entity(tableName = "routine_templates")
data class RoutineTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val isActive: Boolean = true,
    val targetDays: List<String> = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"),
    val workStartHour: Int = 7,
    val workStartMinute: Int = 0,
    val workEndHour: Int = 17,
    val workEndMinute: Int = 0,
    val itemsJson: String = "" // List of RoutineSlot objects encoded as JSON
)

@Serializable
@Entity(
    tableName = "routine_instances",
    indices = [
        Index(value = ["dateString"], unique = true),
        Index(value = ["startOfDayUtc"])
    ]
)
data class RoutineInstanceEntity(
    @PrimaryKey
    val id: String,
    val templateId: String,
    val dateString: String, // "YYYY-MM-DD"
    val startOfDayUtc: Long,
    val totalTasksPlanned: Int = 0,
    val tasksCompleted: Int = 0,
    val completionPercentage: Float = 0f,
    val missedCriticalTaskCount: Int = 0,
    val deviationsRecorded: Int = 0,
    val recoveryPlanGenerated: Boolean = false,
    val dailyReviewNotes: String = ""
)
