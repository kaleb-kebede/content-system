package com.example.creatorgrowthos.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.creatorgrowthos.MainActivity
import com.example.creatorgrowthos.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_CRITICAL = "channel_critical_tasks"
        const val CHANNEL_ROUTINE = "channel_routine_tasks"
        const val CHANNEL_PUBLISHING = "channel_publishing"
        const val CHANNEL_REVIEWS = "channel_reviews"

        const val NOTIF_ID_AGENDA = 1001
        const val NOTIF_ID_TASK_PREFIX = 2000
        const val NOTIF_ID_CRITICAL_PREFIX = 3000
        const val NOTIF_ID_PUBLISHING_PREFIX = 4000
        const val NOTIF_ID_REVIEW = 5001
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val criticalChannel = NotificationChannel(
                CHANNEL_CRITICAL,
                "Critical Tasks & Missed Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High priority alerts requiring explicit recovery decisions"
                enableVibration(true)
            }

            val routineChannel = NotificationChannel(
                CHANNEL_ROUTINE,
                "Routine & Upcoming Tasks",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Upcoming task reminders and agenda updates"
            }

            val publishingChannel = NotificationChannel(
                CHANNEL_PUBLISHING,
                "Publishing Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Due post reminders and publishing confirmations"
                enableVibration(true)
            }

            val reviewChannel = NotificationChannel(
                CHANNEL_REVIEWS,
                "Reviews & Strategy",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily and weekly performance review reminders"
            }

            nm.createNotificationChannels(listOf(criticalChannel, routineChannel, publishingChannel, reviewChannel))
        }
    }

    fun showUpcomingTaskNotification(taskId: String, taskTitle: String, minutesUntil: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "today")
            putExtra("task_id", taskId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ROUTINE)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Upcoming Session in $minutesUntil min")
            .setContentText(taskTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIF_ID_TASK_PREFIX + (taskId.hashCode() % 1000), builder.build())
    }

    fun showCriticalMissedTaskAlert(taskId: String, taskTitle: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "recovery")
            putExtra("task_id", taskId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_CRITICAL)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Action Required: Critical Missed Task")
            .setContentText("'$taskTitle' requires an explicit completion or recovery decision.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIF_ID_CRITICAL_PREFIX + (taskId.hashCode() % 1000), builder.build())
    }

    fun showPublishingDueNotification(publicationId: String, platformName: String, title: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "publish")
            putExtra("publication_id", publicationId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            publicationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_PUBLISHING)
            .setSmallIcon(android.R.drawable.ic_menu_send)
            .setContentTitle("Publishing Due: $platformName")
            .setContentText("Time to publish: '$title'")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(NOTIF_ID_PUBLISHING_PREFIX + (publicationId.hashCode() % 1000), builder.build())
    }
}
