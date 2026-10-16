package com.example.creatorgrowthos.ui.screens.calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToPublish: (publicationId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp)
    ) {
        // Calendar Navigation Header
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCHEDULE & PRODUCTION TIMELINE",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiState.selectedDateString.ifBlank { "Calendar" },
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Slate850,
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousDay() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = Slate300)
                    }
                    IconButton(
                        onClick = { viewModel.goToToday() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Today, contentDescription = "Today", tint = ElectricBluePrimary)
                    }
                    IconButton(
                        onClick = { viewModel.nextDay() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = Slate300)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Scheduled Publications Section
            if (uiState.publicationsForDay.isNotEmpty()) {
                item {
                    Text(
                        text = "Scheduled Publications (${uiState.publicationsForDay.size})",
                        style = Typography.titleMedium,
                        color = AmberWarningLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.publicationsForDay, key = { it.id }) { pub ->
                    val platformColor = when (pub.destination) {
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
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = platformColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(platformColor)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pub.platformSpecificTitle.ifBlank { "Post for ${pub.destination.displayName}" },
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${pub.destination.displayName} • Due ${formatTime(pub.plannedPublicationUtc)} • ${pub.publicationStatus.displayName}",
                                    style = Typography.bodySmall,
                                    color = Slate400
                                )
                            }
                            Button(
                                onClick = { onNavigateToPublish(pub.id) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberWarningLight)
                            ) {
                                Text("Publish", color = Slate950, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Tasks Section
            item {
                Text(
                    text = "Production Sessions (${uiState.tasksForDay.size})",
                    style = Typography.titleMedium,
                    color = ElectricBluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.tasksForDay.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No production sessions scheduled for this day",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate300
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sessions automatically populate from active pipeline workflow items.",
                                style = Typography.bodySmall,
                                color = Slate500
                            )
                        }
                    }
                }
            } else {
                items(uiState.tasksForDay, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isCritical) CrimsonCriticalLight else ElectricBluePrimary)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = task.title,
                                        style = Typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate100
                                    )
                                    if (task.isCritical) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = CrimsonCritical.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "CRITICAL",
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                style = Typography.labelSmall,
                                                fontSize = 9.sp,
                                                color = CrimsonCriticalLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${formatTime(task.scheduledStartTimeUtc)} - ${formatTime(task.scheduledEndTimeUtc)} (${task.estimatedDurationMinutes}m) • ${task.status.displayName}",
                                    style = Typography.bodySmall,
                                    color = Slate400
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

private fun formatTime(utc: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = utc
    return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}

