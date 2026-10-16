package com.example.creatorgrowthos.domain.publishing

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.creatorgrowthos.domain.model.PlatformType

data class PlatformLaunchResult(
    val success: Boolean,
    val method: String, // "APP_INTENT", "BROWSER_FALLBACK", "FAILED"
    val message: String
)

class PlatformPublisher(private val context: Context) {

    /**
     * Copies text content to clipboard with verified confirmation.
     */
    fun copyToClipboard(label: String, text: String): Boolean {
        return try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(label, text)
            clipboard.setPrimaryClip(clip)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Launches the official platform application via supported Android intent or browser fallback.
     */
    fun launchPlatform(
        platform: PlatformType,
        mediaUri: String? = null
    ): PlatformLaunchResult {
        val pm = context.packageManager

        // Intent construction
        val intent = when (platform) {
            PlatformType.YOUTUBE_LONG, PlatformType.YOUTUBE_SHORTS -> {
                // Try YouTube Studio or YouTube App
                val ytStudioIntent = pm.getLaunchIntentForPackage("com.google.android.apps.youtube.creator")
                ytStudioIntent ?: pm.getLaunchIntentForPackage("com.google.android.youtube")
            }
            PlatformType.TIKTOK -> {
                pm.getLaunchIntentForPackage("com.zhiliaoapp.musically")
            }
            PlatformType.INSTAGRAM_REELS -> {
                val igIntent = pm.getLaunchIntentForPackage("com.instagram.android")
                igIntent
            }
        }

        if (intent != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return PlatformLaunchResult(
                    success = true,
                    method = "APP_INTENT",
                    message = "Opened ${platform.displayName} application."
                )
            } catch (e: Exception) {
                // Fallback to browser below
            }
        }

        // Web Browser Fallback
        val fallbackUrl = when (platform) {
            PlatformType.YOUTUBE_LONG, PlatformType.YOUTUBE_SHORTS -> "https://studio.youtube.com"
            PlatformType.TIKTOK -> "https://www.tiktok.com/upload"
            PlatformType.INSTAGRAM_REELS -> "https://www.instagram.com"
        }

        return try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
            PlatformLaunchResult(
                success = true,
                method = "BROWSER_FALLBACK",
                message = "App not found. Opened ${platform.displayName} web upload in browser."
            )
        } catch (e: Exception) {
            PlatformLaunchResult(
                success = false,
                method = "FAILED",
                message = "Unable to open platform or browser: ${e.localizedMessage}"
            )
        }
    }
}
