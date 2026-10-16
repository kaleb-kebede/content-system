package com.example.creatorgrowthos

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.creatorgrowthos.di.appModule
import com.example.creatorgrowthos.di.viewModelModule
import com.example.creatorgrowthos.workers.TaskAuditorWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import java.util.concurrent.TimeUnit

class CreatorGrowthApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize Koin Dependency Injection
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@CreatorGrowthApp)
            modules(appModule, viewModelModule)
        }

        // Schedule periodic task status auditor with WorkManager (every 15 mins)
        setupPeriodicAuditing()
    }

    private fun setupPeriodicAuditing() {
        val auditRequest = PeriodicWorkRequestBuilder<TaskAuditorWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TaskAuditorWork",
            ExistingPeriodicWorkPolicy.KEEP,
            auditRequest
        )
    }
}
