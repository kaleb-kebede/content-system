package com.example.creatorgrowthos.ui.screens.analytics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showManualEntryDialog by remember { mutableStateOf(false) }

    if (showManualEntryDialog) {
        ManualSnapshotEntryDialog(
            onDismiss = { showManualEntryDialog = false },
            onSave = { snapshot ->
                viewModel.recordManualSnapshot(snapshot)
                showManualEntryDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Analytics Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GROWTH & PERFORMANCE",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Analytics Hub",
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }

                Button(
                    onClick = { showManualEntryDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Slate950, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Metrics", color = Slate950, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Platform Comparison Cards
        item {
            Text(
                text = "Platform Performance Comparison",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlatformMetricCard(
                    title = "YouTube",
                    accentColor = YouTubeRed,
                    views = uiState.ytOverview?.totalViews ?: 0L,
                    median = uiState.ytOverview?.medianViews ?: 0L,
                    engagement = uiState.ytOverview?.averageEngagementRate ?: 0f,
                    modifier = Modifier.weight(1f)
                )
                PlatformMetricCard(
                    title = "TikTok",
                    accentColor = TikTokCyan,
                    views = uiState.tiktokOverview?.totalViews ?: 0L,
                    median = uiState.tiktokOverview?.medianViews ?: 0L,
                    engagement = uiState.tiktokOverview?.averageEngagementRate ?: 0f,
                    modifier = Modifier.weight(1f)
                )
                PlatformMetricCard(
                    title = "Reels",
                    accentColor = InstagramPurple,
                    views = uiState.igOverview?.totalViews ?: 0L,
                    median = uiState.igOverview?.medianViews ?: 0L,
                    engagement = uiState.igOverview?.averageEngagementRate ?: 0f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Metric Snapshots History Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Historical Metric Snapshots",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Slate800
                ) {
                    Text(
                        text = "${uiState.recentSnapshots.size} Entries",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = Typography.labelSmall,
                        color = Slate400
                    )
                }
            }
        }

        if (uiState.recentSnapshots.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Slate800,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = Slate500, modifier = Modifier.size(26.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No analytics snapshots recorded yet",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate300
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap 'Log Metrics' above to enter 24h, 48h, or 7D metrics for your published videos.",
                            style = Typography.bodyMedium,
                            color = Slate500
                        )
                    }
                }
            }
        } else {
            items(uiState.recentSnapshots, key = { it.id }) { snapshot ->
                val platformColor = when (snapshot.platform) {
                    PlatformType.YOUTUBE_SHORTS, PlatformType.YOUTUBE_LONG -> YouTubeRed
                    PlatformType.TIKTOK -> TikTokCyan
                    PlatformType.INSTAGRAM_REELS -> InstagramPurple
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = platformColor.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = snapshot.platform.shortName,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = Typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = platformColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = snapshot.platform.displayName,
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate800
                            ) {
                                Text(
                                    text = snapshot.measurementWindow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = Typography.labelSmall,
                                    color = ElectricBluePrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate800,
                                modifier = Modifier.weight(1f).padding(end = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = "Views", style = Typography.labelSmall, color = Slate400)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = snapshot.views?.toString() ?: "N/A",
                                        style = Typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate100
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate800,
                                modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = "Engagement", style = Typography.labelSmall, color = Slate400)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = snapshot.calculatedEngagementRate?.let { String.format("%.1f%%", it) } ?: "N/A",
                                        style = Typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MintProgressLight
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Slate800,
                                modifier = Modifier.weight(1f).padding(start = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = "Likes / Comm", style = Typography.labelSmall, color = Slate400)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${snapshot.likes ?: 0} / ${snapshot.comments ?: 0}",
                                        style = Typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate100
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun PlatformMetricCard(
    title: String,
    accentColor: Color,
    views: Long,
    median: Long,
    engagement: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Slate850),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = accentColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = Typography.labelSmall,
                    fontSize = 10.sp,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${views}",
                style = Typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )
            Text(
                text = "total views",
                style = Typography.labelSmall,
                color = Slate500,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Med: $median",
                    style = Typography.bodySmall,
                    color = Slate400,
                    fontSize = 11.sp
                )
                Text(
                    text = "${String.format("%.1f", engagement)}%",
                    style = Typography.labelSmall,
                    color = MintProgressLight,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualSnapshotEntryDialog(
    onDismiss: () -> Unit,
    onSave: (AnalyticsSnapshotEntity) -> Unit
) {
    var selectedPlatform by remember { mutableStateOf(PlatformType.YOUTUBE_SHORTS) }
    var window by remember { mutableStateOf("48H") }
    var viewsInput by remember { mutableStateOf("") }
    var likesInput by remember { mutableStateOf("") }
    var commentsInput by remember { mutableStateOf("") }
    var sharesInput by remember { mutableStateOf("") }
    var followersInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Performance Metrics", style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Platform:", style = Typography.labelSmall, color = Slate400)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(PlatformType.YOUTUBE_SHORTS, PlatformType.TIKTOK, PlatformType.INSTAGRAM_REELS).forEach { p ->
                        FilterChip(
                            selected = selectedPlatform == p,
                            onClick = { selectedPlatform = p },
                            label = { Text(p.shortName, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricBluePrimary,
                                selectedLabelColor = Slate950,
                                containerColor = Slate800,
                                labelColor = Slate300
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = viewsInput,
                    onValueChange = { viewsInput = it },
                    label = { Text("Views") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = likesInput,
                        onValueChange = { likesInput = it },
                        label = { Text("Likes") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = BorderDefault,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = commentsInput,
                        onValueChange = { commentsInput = it },
                        label = { Text("Comments") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = BorderDefault,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sharesInput,
                        onValueChange = { sharesInput = it },
                        label = { Text("Shares/Saves") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = BorderDefault,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = followersInput,
                        onValueChange = { followersInput = it },
                        label = { Text("Subs/Followers") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBluePrimary,
                            unfocusedBorderColor = BorderDefault,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val snapshot = AnalyticsSnapshotEntity(
                        id = "",
                        publicationId = "manual_${System.currentTimeMillis()}",
                        platform = selectedPlatform,
                        measurementWindow = window,
                        views = viewsInput.toLongOrNull(),
                        likes = likesInput.toLongOrNull(),
                        comments = commentsInput.toLongOrNull(),
                        shares = sharesInput.toLongOrNull(),
                        subscribersGained = followersInput.toIntOrNull(),
                        followersGained = followersInput.toIntOrNull()
                    )
                    onSave(snapshot)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Snapshot", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate400)
            }
        },
        containerColor = Slate900,
        shape = RoundedCornerShape(20.dp)
    )
}

