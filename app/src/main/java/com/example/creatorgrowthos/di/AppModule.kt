package com.example.creatorgrowthos.di

import com.example.creatorgrowthos.data.local.AppDatabase
import com.example.creatorgrowthos.data.repository.*
import com.example.creatorgrowthos.domain.publishing.PlatformPublisher
import com.example.creatorgrowthos.domain.scheduler.SchedulingEngine
import com.example.creatorgrowthos.notifications.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Database
    single { AppDatabase.buildDatabase(androidContext()) }

    // DAOs
    single { get<AppDatabase>().contentIdeaDao() }
    single { get<AppDatabase>().contentItemDao() }
    single { get<AppDatabase>().contentDerivativeDao() }
    single { get<AppDatabase>().scriptDao() }
    single { get<AppDatabase>().publicationDao() }
    single { get<AppDatabase>().taskDao() }
    single { get<AppDatabase>().scheduleBlockDao() }
    single { get<AppDatabase>().routineDao() }
    single { get<AppDatabase>().workflowAndChecklistDao() }
    single { get<AppDatabase>().analyticsSnapshotDao() }
    single { get<AppDatabase>().platformAccountDao() }
    single { get<AppDatabase>().contentPillarDao() }
    single { get<AppDatabase>().goalDao() }
    single { get<AppDatabase>().reviewDao() }
    single { get<AppDatabase>().additionalModulesDao() }

    // Domain & Utilities
    single { SchedulingEngine() }
    single { PlatformPublisher(androidContext()) }
    single { NotificationHelper(androidContext()) }

    // Repositories
    single { IdeasRepository(get(), get(), get()) }
    single { ContentRepository(get(), get(), get()) }
    single { TaskScheduleRepository(get(), get(), get(), get()) }
    single { PublishingRepository(get(), get(), get(), get()) }
    single { ScriptRepository(get()) }
    single { AnalyticsRepository(get()) }
    single { StrategyGoalsRepository(get(), get(), get(), get()) }
    single { BackupRestoreRepository(get()) }
    single { SettingsRepository(get()) }
    single { RepurposingRepository(get(), get(), get()) }
}
