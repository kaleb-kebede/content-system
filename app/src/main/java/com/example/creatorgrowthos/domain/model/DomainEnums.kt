package com.example.creatorgrowthos.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class WorkflowStage(val displayName: String, val stepOrder: Int) {
    IDEA("Idea", 0),
    VALIDATION("Validation", 1),
    PLANNED("Planned", 2),
    RESEARCH("Research", 3),
    SCRIPTING("Scripting", 4),
    READY_TO_RECORD("Ready to Record", 5),
    RECORDING("Recording", 6),
    EDITING("Editing", 7),
    QUALITY_REVIEW("Quality Review", 8),
    APPROVED("Approved", 9),
    SCHEDULED("Scheduled", 10),
    PUBLISHED("Published", 11),
    ANALYZING("Analyzing", 12),
    ARCHIVED("Archived", 13),
    BLOCKED("Blocked", 99),
    CANCELLED("Cancelled", 100);

    fun isTerminal(): Boolean = this == PUBLISHED || this == ARCHIVED || this == CANCELLED
    fun canSchedule(): Boolean = this == APPROVED || this == SCHEDULED
}

@Serializable
enum class TaskStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    ACTIVE("In Progress"),
    PAUSED("Paused"),
    COMPLETED("Completed"),
    MISSED("Missed"),
    OVERDUE("Overdue"),
    BLOCKED("Blocked"),
    SKIPPED("Skipped");

    fun isFinal(): Boolean = this == COMPLETED || this == SKIPPED
    fun requiresDecision(): Boolean = this == MISSED || this == OVERDUE || this == BLOCKED
}

@Serializable
enum class PublicationStatus(val displayName: String) {
    DRAFT("Draft"),
    READY("Ready"),
    SCHEDULED_IN_APP("Scheduled in App"),
    REMINDER_SENT("Reminder Sent"),
    PUBLISHING_IN_PROGRESS("Publishing in Progress"),
    PUBLISHED_CONFIRMED("Published (Confirmed)"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    fun isConfirmed(): Boolean = this == PUBLISHED_CONFIRMED
}

@Serializable
enum class PlatformType(val displayName: String, val packageName: String, val shortName: String) {
    YOUTUBE_LONG("YouTube (Long-form)", "com.google.android.youtube", "YT Long"),
    YOUTUBE_SHORTS("YouTube Shorts", "com.google.android.youtube", "Shorts"),
    TIKTOK("TikTok", "com.zhiliaoapp.musically", "TikTok"),
    INSTAGRAM_REELS("Instagram Reels", "com.instagram.android", "Reels");

    fun isShortForm(): Boolean = this != YOUTUBE_LONG
}

@Serializable
enum class TaskPriority(val level: Int, val label: String) {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical");

    fun isCritical(): Boolean = this == CRITICAL
}

@Serializable
enum class ContentFormat(val displayName: String) {
    LONG_FORM_VIDEO("Long-form Video"),
    SHORT_FORM_VIDEO("Short-form Video"),
    CAROUSEL("Carousel"),
    STORY("Story");
}

@Serializable
enum class RecoveryActionType {
    RESCHEDULE,
    SPLIT_TASK,
    MOVE_DEPENDENCIES,
    MARK_BLOCKED,
    SKIP_WITH_REASON,
    EMERGENCY_OVERRIDE
}
