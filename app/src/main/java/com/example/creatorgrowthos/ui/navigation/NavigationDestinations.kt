package com.example.creatorgrowthos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ViewKanban
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Primary Top-level
    object Today : Screen("today", "Today", Icons.Default.Dashboard)
    object Pipeline : Screen("pipeline", "Pipeline", Icons.Default.ViewKanban)
    object Calendar : Screen("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object More : Screen("more", "More", Icons.Default.MoreHoriz)

    // Secondary / Detail Destinations
    object IdeasVault : Screen("ideas_vault", "Ideas Vault")
    object IdeaDetail : Screen("idea_detail/{ideaId}", "Idea Detail") {
        fun createRoute(ideaId: String) = "idea_detail/$ideaId"
    }

    object ContentDetail : Screen("content_detail/{contentId}", "Content Details") {
        fun createRoute(contentId: String) = "content_detail/$contentId"
    }

    object ScriptEditor : Screen("script_editor/{scriptId}?contentId={contentId}", "Script Editor") {
        fun createRoute(scriptId: String = "new", contentId: String? = null) =
            if (contentId != null) "script_editor/$scriptId?contentId=$contentId" else "script_editor/$scriptId"
    }

    object Repurposing : Screen("repurposing", "Repurposing")
    object RepurposeDetail : Screen("repurpose_detail/{masterContentId}", "Repurpose Content") {
        fun createRoute(masterContentId: String) = "repurpose_detail/$masterContentId"
    }

    object AssistedPublishing : Screen("assisted_publishing/{publicationId}", "Publish Post") {
        fun createRoute(publicationId: String) = "assisted_publishing/$publicationId"
    }

    object ContentPillars : Screen("content_pillars", "Content Pillars")
    object GoalsStrategy : Screen("goals_strategy", "Goals & Strategy")
    object ContentTemplates : Screen("content_templates", "Templates")
    object ResearchLibrary : Screen("research_library", "Research")
    object BrandAssets : Screen("brand_assets", "Brand Assets")
    object ProductIdeas : Screen("product_ideas", "Product Ideas")
    object Monetization : Screen("monetization", "Monetization")
    object Reviews : Screen("reviews", "Daily & Weekly Reviews")
    object BackupRestore : Screen("backup_restore", "Backup & Restore")
    object Settings : Screen("settings", "Settings")

    companion object {
        val bottomNavItems = listOf(Today, Pipeline, Calendar, Analytics, More)
    }
}
