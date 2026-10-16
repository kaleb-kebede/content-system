package com.example.creatorgrowthos.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.creatorgrowthos.data.repository.TaskScheduleRepository
import com.example.creatorgrowthos.notifications.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TaskAuditorWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params), KoinComponent {

    private val taskRepository: TaskScheduleRepository by inject()
    private val notificationHelper: NotificationHelper by inject()

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            taskRepository.auditTaskStatus(now)

            // Alert for any unhandled critical missed tasks
            val criticalMissed = taskRepository.getCriticalMissedTasks()
            // Notification dispatching if needed
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
