package com.example.creatorgrowthos.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.creatorgrowthos.theme.*
import com.example.creatorgrowthos.ui.navigation.Screen
import com.example.creatorgrowthos.ui.screens.analytics.AnalyticsScreen
import com.example.creatorgrowthos.ui.screens.backup.BackupRestoreScreen
import com.example.creatorgrowthos.ui.screens.calendar.CalendarScreen
import com.example.creatorgrowthos.ui.screens.extra.ExtrasScreen
import com.example.creatorgrowthos.ui.screens.goals.GoalsStrategyScreen
import com.example.creatorgrowthos.ui.screens.ideas.IdeasVaultScreen
import com.example.creatorgrowthos.ui.screens.more.MoreScreen
import com.example.creatorgrowthos.ui.screens.pipeline.PipelineScreen
import com.example.creatorgrowthos.ui.screens.publishing.AssistedPublishingScreen
import com.example.creatorgrowthos.ui.screens.repurposing.RepurposingScreen
import com.example.creatorgrowthos.ui.screens.script.ScriptEditorScreen
import com.example.creatorgrowthos.ui.screens.settings.SettingsScreen
import com.example.creatorgrowthos.ui.screens.today.TodayScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var showQuickCreate by remember { mutableStateOf(false) }

    val isTopLevelDestination = Screen.bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Slate900,
        floatingActionButton = {
            if (isTopLevelDestination) {
                FloatingActionButton(
                    onClick = { showQuickCreate = true },
                    containerColor = ElectricBluePrimary,
                    contentColor = Slate950,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Quick Create"
                    )
                }
            }
        },
        bottomBar = {
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = Slate950,
                    contentColor = Slate300,
                    tonalElevation = 8.dp
                ) {
                    Screen.bottomNavItems.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                screen.icon?.let { icon ->
                                    Icon(imageVector = icon, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title, style = Typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElectricBluePrimary,
                                selectedTextColor = ElectricBluePrimary,
                                unselectedIconColor = Slate500,
                                unselectedTextColor = Slate500,
                                indicatorColor = Slate850
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Primary Destinations
            composable(Screen.Today.route) {
                TodayScreen(
                    viewModel = koinViewModel(),
                    onNavigateToPublish = { pubId ->
                        navController.navigate(Screen.AssistedPublishing.createRoute(pubId))
                    },
                    onNavigateToPipeline = {
                        navController.navigate(Screen.Pipeline.route)
                    }
                )
            }

            composable(Screen.Pipeline.route) {
                PipelineScreen(
                    viewModel = koinViewModel(),
                    onNavigateToScript = { contentId ->
                        navController.navigate(Screen.ScriptEditor.createRoute("new", contentId))
                    }
                )
            }

            composable(Screen.Calendar.route) {
                CalendarScreen(
                    viewModel = koinViewModel(),
                    onNavigateToPublish = { pubId ->
                        navController.navigate(Screen.AssistedPublishing.createRoute(pubId))
                    }
                )
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    viewModel = koinViewModel()
                )
            }

            composable(Screen.More.route) {
                MoreScreen(
                    onNavigate = { route ->
                        navController.navigate(route)
                    }
                )
            }

            // Secondary / Creator Engine Destinations
            composable(Screen.IdeasVault.route) {
                IdeasVaultScreen(
                    viewModel = koinViewModel(),
                    onNavigateToPipeline = {
                        navController.navigate(Screen.Pipeline.route)
                    }
                )
            }

            composable(
                route = "script_editor/{scriptId}?contentId={contentId}",
                arguments = listOf(
                    navArgument("scriptId") {
                        type = NavType.StringType
                        defaultValue = "new"
                    },
                    navArgument("contentId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val scriptId = backStackEntry.arguments?.getString("scriptId") ?: "new"
                val contentId = backStackEntry.arguments?.getString("contentId")
                ScriptEditorScreen(
                    scriptId = scriptId,
                    contentItemId = contentId,
                    viewModel = koinViewModel()
                )
            }

            composable(
                route = "assisted_publishing/{publicationId}",
                arguments = listOf(
                    navArgument("publicationId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val publicationId = backStackEntry.arguments?.getString("publicationId") ?: ""
                AssistedPublishingScreen(
                    publicationId = publicationId,
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Repurposing.route) {
                RepurposingScreen(
                    viewModel = koinViewModel()
                )
            }

            composable(Screen.GoalsStrategy.route) {
                GoalsStrategyScreen(
                    viewModel = koinViewModel()
                )
            }

            composable(Screen.BackupRestore.route) {
                BackupRestoreScreen(
                    viewModel = koinViewModel()
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    viewModel = koinViewModel()
                )
            }

            // Additional Modules
            composable(Screen.ContentPillars.route) {
                ExtrasScreen(
                    initialRouteKey = "content_pillars",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ContentTemplates.route) {
                ExtrasScreen(
                    initialRouteKey = "content_templates",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ResearchLibrary.route) {
                ExtrasScreen(
                    initialRouteKey = "research_library",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BrandAssets.route) {
                ExtrasScreen(
                    initialRouteKey = "brand_assets",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ProductIdeas.route) {
                ExtrasScreen(
                    initialRouteKey = "product_ideas",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Monetization.route) {
                ExtrasScreen(
                    initialRouteKey = "monetization",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reviews.route) {
                ExtrasScreen(
                    initialRouteKey = "reviews",
                    viewModel = koinViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

    if (showQuickCreate) {
        QuickCreateBottomSheet(
            onDismiss = { showQuickCreate = false },
            onNewIdea = {
                navController.navigate(Screen.IdeasVault.route)
            },
            onNewScript = {
                navController.navigate(Screen.ScriptEditor.createRoute("new"))
            },
            onSchedulePost = {
                navController.navigate(Screen.Calendar.route)
            }
        )
    }
}
