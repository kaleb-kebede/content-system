package com.example.creatorgrowthos.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// SURFACE & BACKGROUND TOKENS (Dark / OLED first)
// ==========================================
val Slate950 = Color(0xFF070B14) // Deepest OLED background
val Slate900 = Color(0xFF0C1322) // Primary screen background
val Slate850 = Color(0xFF131D31) // Primary card / container surface
val Slate800 = Color(0xFF1A263E) // Elevated card / modal surface
val Slate750 = Color(0xFF22314E) // High-elevation interactive surface
val Slate700 = Color(0xFF2C3E63) // Outline / Border default
val Slate650 = Color(0xFF3B507D) // Border subtle highlight
val Slate600 = Color(0xFF506898) // Secondary / inactive icon
val Slate500 = Color(0xFF6E84B0) // Muted text / placeholder
val Slate400 = Color(0xFF94A7CB) // Secondary body text (high readability)
val Slate300 = Color(0xFFC0CEE3) // Primary body text / subtle heading
val Slate200 = Color(0xFFE2EAF4) // Bright text
val Slate100 = Color(0xFFF4F7FB) // Highest contrast white-ish

// Surface Semantic Mapping
val SurfaceDarkLowest = Slate950
val SurfaceDarkDefault = Slate900
val SurfaceDarkElevated = Slate850
val SurfaceDarkContainer = Slate800
val SurfaceDarkContainerHigh = Slate750

// Borders & Dividers
val BorderDefault = Color(0xFF1E2D4A)
val BorderSubtle = Color(0xFF16233B)
val BorderActive = Color(0xFF38BDF8)
val BorderGlass = Color(0x3338BDF8)

// ==========================================
// BRAND & ACCENT TOKENS
// ==========================================
val ElectricBluePrimary = Color(0xFF38BDF8)
val ElectricBlueLight = Color(0xFF7DD3FC)
val ElectricBlueDark = Color(0xFF0284C7)
val ElectricBlueGlow = Color(0xFF0EA5E9)

val MintProgress = Color(0xFF10B981)
val MintProgressLight = Color(0xFF34D399)
val MintSuccess = MintProgress
val MintSuccessLight = MintProgressLight
val MintProgressGlow = Color(0xFF059669)

val AmberWarning = Color(0xFFF59E0B)
val AmberWarningLight = Color(0xFFFBBF24)
val AmberWarningDark = Color(0xFFD97706)

val CrimsonCritical = Color(0xFFEF4444)
val CrimsonCriticalLight = Color(0xFFF87171)
val CrimsonCriticalDark = Color(0xFFDC2626)

val VioletSecondary = Color(0xFF8B5CF6)
val PurpleAccent = Color(0xFFA855F7)
val PurpleAccentLight = Color(0xFFC084FC)
val PurpleLight = PurpleAccentLight

// ==========================================
// PLATFORM BRANDED COLORS
// ==========================================
val YouTubeRed = Color(0xFFFF0000)
val YouTubeRedSubtle = Color(0x26FF0000)
val TikTokCyan = Color(0xFF00F2FE)
val TikTokPink = Color(0xFFFE2C55)
val InstagramPurple = Color(0xFFC13584)
val InstagramOrange = Color(0xFFF56040)
val InstagramYellow = Color(0xFFFCAF45)

// ==========================================
// DESIGN SYSTEM GRADIENT BRUSHES
// ==========================================
val HeroCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF152238),
        Color(0xFF0D1728)
    )
)

val ActiveSessionGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF132A44),
        Color(0xFF0D1D33)
    )
)

val ElectricButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF38BDF8),
        Color(0xFF0284C7)
    )
)

val MintButtonGradient = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF34D399),
        Color(0xFF059669)
    )
)

val CrimsonAlertGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x33EF4444),
        Color(0x1AEF4444)
    )
)

val PurpleCreationGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFA855F7),
        Color(0xFF7C3AED)
    )
)

val GlassBorderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0x6638BDF8),
        Color(0x1A38BDF8),
        Color(0x0DFFFFFF)
    )
)

// ==========================================
// LIGHT THEME PALETTE
// ==========================================
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightBorder = Color(0xFFCBD5E1)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightPrimary = Color(0xFF0284C7)

