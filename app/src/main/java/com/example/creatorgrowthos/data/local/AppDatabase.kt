package com.example.creatorgrowthos.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.creatorgrowthos.data.local.dao.*
import com.example.creatorgrowthos.data.local.entity.*
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        ContentIdeaEntity::class,
        ContentItemEntity::class,
        ContentDerivativeEntity::class,
        ScriptEntity::class,
        ScriptSectionEntity::class,
        ContentPublicationEntity::class,
        ContentTaskEntity::class,
        ScheduleBlockEntity::class,
        RoutineTemplateEntity::class,
        RoutineInstanceEntity::class,
        WorkflowTransitionEntity::class,
        ChecklistEntity::class,
        ChecklistItemEntity::class,
        AnalyticsSnapshotEntity::class,
        PlatformAccountEntity::class,
        ContentPillarEntity::class,
        ContentTagEntity::class,
        GoalEntity::class,
        ReviewEntity::class,
        ProductIdeaEntity::class,
        MonetizationRecordEntity::class,
        BrandAssetEntity::class,
        ResearchResourceEntity::class,
        ContentTemplateEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentIdeaDao(): ContentIdeaDao
    abstract fun contentItemDao(): ContentItemDao
    abstract fun contentDerivativeDao(): ContentDerivativeDao
    abstract fun scriptDao(): ScriptDao
    abstract fun publicationDao(): PublicationDao
    abstract fun taskDao(): TaskDao
    abstract fun scheduleBlockDao(): ScheduleBlockDao
    abstract fun routineDao(): RoutineDao
    abstract fun workflowAndChecklistDao(): WorkflowAndChecklistDao
    abstract fun analyticsSnapshotDao(): AnalyticsSnapshotDao
    abstract fun platformAccountDao(): PlatformAccountDao
    abstract fun contentPillarDao(): ContentPillarDao
    abstract fun goalDao(): GoalDao
    abstract fun reviewDao(): ReviewDao
    abstract fun additionalModulesDao(): AdditionalModulesDao

    companion object {
        const val DATABASE_NAME = "creator_growth_os.db"

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Prepopulate onboarding data on background thread
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateDefaults(context)
                    }
                }
            }).build()
        }

        suspend fun prepopulateDefaults(context: Context) {
            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()

            try {
                // 1. Initial Platform Accounts (Section 2, 9, 17)
                val initialAccounts = listOf(
                    PlatformAccountEntity(
                        id = "acc_yt_long",
                        platform = PlatformType.YOUTUBE_LONG,
                        accountHandle = "@CreatorChannel",
                        currentAudienceCount = 0,
                        targetAudienceCount = 100000,
                        weeklyPublishingTarget = 1
                    ),
                    PlatformAccountEntity(
                        id = "acc_yt_shorts",
                        platform = PlatformType.YOUTUBE_SHORTS,
                        accountHandle = "@CreatorShorts",
                        currentAudienceCount = 0,
                        targetAudienceCount = 100000,
                        weeklyPublishingTarget = 5
                    ),
                    PlatformAccountEntity(
                        id = "acc_tiktok",
                        platform = PlatformType.TIKTOK,
                        accountHandle = "@creator_daily",
                        currentAudienceCount = 0,
                        targetAudienceCount = 100000,
                        weeklyPublishingTarget = 5
                    ),
                    PlatformAccountEntity(
                        id = "acc_ig_reels",
                        platform = PlatformType.INSTAGRAM_REELS,
                        accountHandle = "@creator.growth",
                        currentAudienceCount = 0,
                        targetAudienceCount = 100000,
                        weeklyPublishingTarget = 3
                    )
                )
                db.platformAccountDao().insertAccounts(initialAccounts)

                // 2. Initial Content Pillars (Section 10, 17)
                val initialPillars = listOf(
                    ContentPillarEntity(
                        id = "pillar_tactics",
                        name = "Actionable Tactics",
                        description = "Step-by-step technical & practical workflows",
                        colorHex = "#38BDF8",
                        targetPercentage = 40f
                    ),
                    ContentPillarEntity(
                        id = "pillar_mindset",
                        name = "Creator Mindset & Discipline",
                        description = "Daily routines, overcoming burnout, focus",
                        colorHex = "#34D399",
                        targetPercentage = 25f
                    ),
                    ContentPillarEntity(
                        id = "pillar_breakdowns",
                        name = "Case Studies & Breakdowns",
                        description = "Analyzing high-growth creators and viral videos",
                        colorHex = "#8B5CF6",
                        targetPercentage = 20f
                    ),
                    ContentPillarEntity(
                        id = "pillar_tools",
                        name = "Tools & Systems",
                        description = "Software, productivity stacks, automation",
                        colorHex = "#F59E0B",
                        targetPercentage = 15f
                    )
                )
                db.contentPillarDao().insertPillars(initialPillars)

                // 3. Initial Default Routine Template (Section 8 Onboarding Template)
                val defaultRoutine = RoutineTemplateEntity(
                    id = "template_default_routine",
                    name = "Standard Daily Creator Routine",
                    isActive = true,
                    targetDays = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"),
                    workStartHour = 7,
                    workStartMinute = 0,
                    workEndHour = 17,
                    workEndMinute = 0,
                    itemsJson = """[
                        {"startMin":0,"durationMin":15,"title":"Review agenda and select priorities","critical":true,"workflowStage":"PLANNED"},
                        {"startMin":15,"durationMin":30,"title":"Research and idea validation","critical":false,"workflowStage":"RESEARCH"},
                        {"startMin":45,"durationMin":45,"title":"Script development","critical":true,"workflowStage":"SCRIPTING"},
                        {"startMin":90,"durationMin":30,"title":"Break / buffer window","critical":false,"workflowStage":null},
                        {"startMin":120,"durationMin":60,"title":"Recording or voice-over session","critical":true,"workflowStage":"RECORDING"},
                        {"startMin":180,"durationMin":90,"title":"Video Editing & timeline rough cut","critical":true,"workflowStage":"EDITING"},
                        {"startMin":270,"durationMin":30,"title":"Thumbnail, captions, and metadata","critical":false,"workflowStage":"EDITING"},
                        {"startMin":300,"durationMin":15,"title":"Quality review & final check","critical":true,"workflowStage":"QUALITY_REVIEW"},
                        {"startMin":315,"durationMin":15,"title":"Publishing preparation & distribution","critical":true,"workflowStage":"SCHEDULED"},
                        {"startMin":540,"durationMin":20,"title":"Performance & analytics review","critical":false,"workflowStage":"ANALYZING"},
                        {"startMin":560,"durationMin":10,"title":"Plan tomorrow's production agenda","critical":true,"workflowStage":"PLANNED"}
                    ]"""
                )
                db.routineDao().insertTemplate(defaultRoutine)

                // 4. Initial App Settings (Section 2)
                val defaultSettings = AppSettingsEntity(
                    id = "user_settings",
                    scheduleEnforcementMode = "STRICT",
                    publishingMode = "ASSISTED",
                    dailyRoutineStartHour = 7,
                    dailyRoutineStartMinute = 0,
                    dailyRoutineEndHour = 17,
                    dailyRoutineEndMinute = 0,
                    quietHoursStartHour = 22,
                    quietHoursEndHour = 6,
                    notificationsEnabled = true,
                    morningAgendaReminderEnabled = true,
                    upcomingTaskReminderMinutes = 10,
                    criticalMissedTaskAlertsEnabled = true,
                    publishingReminderEnabled = true,
                    narrationWordsPerMinute = 140,
                    bufferTimeBetweenTasksMinutes = 15,
                    autoSplitLargeTasks = false
                )
                db.additionalModulesDao().saveSettings(defaultSettings)

                // 5. Initial Content Templates (Section 12, 18)
                val defaultTemplates = listOf(
                    ContentTemplateEntity(
                        id = "tpl_short_hook_story",
                        name = "Viral Short-Form Framework",
                        format = "SHORT_FORM",
                        structureOutline = "1. Visceral Hook (0-3s)\n2. Curiosity Gap (3-8s)\n3. Core Insight/Demo (8-25s)\n4. Visual Progression (25-45s)\n5. Payoff / CTA (45-60s)",
                        hookFormula = "Stop doing [Common Mistake] if you want [Desirable Result]",
                        ctaFormula = "Follow for daily creator operating systems.",
                        isSystemPreset = true
                    ),
                    ContentTemplateEntity(
                        id = "tpl_long_breakdown",
                        name = "Deep Dive Long-form YouTube",
                        format = "LONG_FORM_YOUTUBE",
                        structureOutline = "1. Opening Hook & Stakes (0-45s)\n2. Context & Problem (45s-2m)\n3. Core Framework / Principles (2m-8m)\n4. Real World Case Studies (8m-15m)\n5. Climax / Transformation Insight (15m-18m)\n6. Conclusion & Clear Next Action (18m-20m)",
                        hookFormula = "In this video, I break down the exact system behind [Result]...",
                        ctaFormula = "Get the complete template in the description below.",
                        isSystemPreset = true
                    )
                )
                db.additionalModulesDao().insertTemplates(defaultTemplates)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.close()
            }
        }
    }
}
