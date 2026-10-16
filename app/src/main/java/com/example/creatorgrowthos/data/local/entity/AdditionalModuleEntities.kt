package com.example.creatorgrowthos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "product_ideas")
data class ProductIdeaEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val productType: String = "COURSE", // COURSE, DIGITAL_PRODUCT, COMMUNITY, COACHING, NEWSLETTER
    val targetAudience: String = "",
    val priceEstimate: Double = 0.0,
    val validationStatus: String = "VALIDATING", // IDEA, VALIDATING, BUILDING, LAUNCHED
    val notes: String = "",
    val createdAtUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "monetization_records")
data class MonetizationRecordEntity(
    @PrimaryKey
    val id: String,
    val sourceName: String, // AD_REVENUE, SPONSORSHIP, AFFILIATE, DIGITAL_PRODUCT, CONSULTING
    val amount: Double,
    val currency: String = "USD",
    val dateUtc: Long = System.currentTimeMillis(),
    val relatedContentItemId: String? = null,
    val notes: String = ""
)

@Serializable
@Entity(tableName = "brand_assets")
data class BrandAssetEntity(
    @PrimaryKey
    val id: String,
    val assetName: String,
    val assetType: String, // COLOR_PALETTE, TYPOGRAPHY, LOGO_URI, AUDIO_STINGER, BRAND_VOICE, HOOK_TEMPLATE
    val assetValue: String,
    val description: String = ""
)

@Serializable
@Entity(tableName = "research_resources")
data class ResearchResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val url: String = "",
    val summary: String = "",
    val category: String = "COMPETITOR_ANALYSIS", // INDUSTRY_NEWS, COMPETITOR_ANALYSIS, VIRAL_FORMAT, CASE_STUDY
    val keyTakeaways: List<String> = emptyList(),
    val createdAtUtc: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "content_templates")
data class ContentTemplateEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val format: String = "SHORT_FORM", // SHORT_FORM, LONG_FORM_YOUTUBE
    val structureOutline: String,
    val hookFormula: String = "",
    val ctaFormula: String = "",
    val isSystemPreset: Boolean = false
)

@Serializable
@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: String = "user_settings",
    val scheduleEnforcementMode: String = "STRICT",
    val publishingMode: String = "ASSISTED",
    val dailyRoutineStartHour: Int = 7,
    val dailyRoutineStartMinute: Int = 0,
    val dailyRoutineEndHour: Int = 17,
    val dailyRoutineEndMinute: Int = 0,
    val quietHoursStartHour: Int = 22,
    val quietHoursEndHour: Int = 6,
    val notificationsEnabled: Boolean = true,
    val morningAgendaReminderEnabled: Boolean = true,
    val upcomingTaskReminderMinutes: Int = 10,
    val criticalMissedTaskAlertsEnabled: Boolean = true,
    val publishingReminderEnabled: Boolean = true,
    val narrationWordsPerMinute: Int = 140,
    val bufferTimeBetweenTasksMinutes: Int = 15,
    val autoSplitLargeTasks: Boolean = false
)
