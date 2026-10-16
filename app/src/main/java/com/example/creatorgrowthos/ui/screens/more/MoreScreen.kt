package com.example.creatorgrowthos.ui.screens.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.theme.*
import com.example.creatorgrowthos.ui.navigation.Screen

data class MoreModuleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val containerColor: Color,
    val route: String
)

@Composable
fun MoreScreen(
    onNavigate: (route: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val modules = listOf(
        MoreModuleItem("Ideas Vault", "Capture & score viral ideas", Icons.Default.Lightbulb, ElectricBluePrimary, ElectricBluePrimary.copy(alpha = 0.15f), Screen.IdeasVault.route),
        MoreModuleItem("Script Hub", "Long & short form editors", Icons.Default.EditNote, PurpleAccentLight, PurpleAccent.copy(alpha = 0.15f), Screen.ScriptEditor.createRoute("new")),
        MoreModuleItem("Repurposing", "1-to-many platform clips", Icons.Default.Transform, MintProgressLight, MintProgress.copy(alpha = 0.15f), Screen.Repurposing.route),
        MoreModuleItem("Goals & 100K", "Audience targets & pillars", Icons.Default.Flag, AmberWarningLight, AmberWarning.copy(alpha = 0.15f), Screen.GoalsStrategy.route),
        MoreModuleItem("Templates", "Workflow & script systems", Icons.Default.Architecture, ElectricBlueLight, ElectricBluePrimary.copy(alpha = 0.12f), Screen.ContentTemplates.route),
        MoreModuleItem("Research", "Competitive analysis & hooks", Icons.Default.Search, VioletSecondary, VioletSecondary.copy(alpha = 0.15f), Screen.ResearchLibrary.route),
        MoreModuleItem("Brand Assets", "Colors, typography, voice", Icons.Default.Palette, PurpleAccent, PurpleAccent.copy(alpha = 0.15f), Screen.BrandAssets.route),
        MoreModuleItem("Product Ideas", "Courses, digital products", Icons.Default.ShoppingBag, MintProgress, MintProgress.copy(alpha = 0.15f), Screen.ProductIdeas.route),
        MoreModuleItem("Monetization", "Revenue & sponsor tracker", Icons.Default.AttachMoney, AmberWarning, AmberWarning.copy(alpha = 0.15f), Screen.Monetization.route),
        MoreModuleItem("Reviews", "Daily & weekly reviews", Icons.Default.RateReview, ElectricBluePrimary, ElectricBluePrimary.copy(alpha = 0.12f), Screen.Reviews.route),
        MoreModuleItem("Backup & Restore", "JSON & CSV data export", Icons.Default.Backup, Slate300, Slate700.copy(alpha = 0.4f), Screen.BackupRestore.route),
        MoreModuleItem("Settings", "Routines & notification times", Icons.Default.Settings, Slate400, Slate700.copy(alpha = 0.4f), Screen.Settings.route)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Column {
            Text(
                text = "CREATOR SYSTEM MODULES",
                style = Typography.labelSmall,
                color = ElectricBluePrimary,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "System Hub",
                style = Typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { mod ->
                Surface(
                    onClick = { onNavigate(mod.route) },
                    shape = RoundedCornerShape(18.dp),
                    color = Slate850,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = mod.containerColor,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = mod.icon,
                                    contentDescription = null,
                                    tint = mod.accentColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = mod.title,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = mod.description,
                            style = Typography.bodySmall,
                            color = Slate400,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

