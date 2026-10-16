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
        Spacer(modifier = Modifier.height(8.dp))
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
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.selectedDateString.ifBlank { "Calendar" },
                    style = Typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { viewModel.previousDay() }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = Slate200)
                }
                IconButton(onClick = { viewModel.goToToday() }) {
                    Icon(Icons.Default.Today, contentDescription = "Today", tint = ElectricBluePrimary)
                }
                IconButton(onClick = { viewModel.nextDay() }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Day", tint = Slate200)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Scheduled Publications Section
            if (uiState.publicationsForDay.isNotEmpty()) {
                item {
                    Text(
                        text = "Scheduled Publications (${uiState.publicationsForDay.size})",
                        style = Typography.labelLarge,
                        color = AmberWarningLight,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(uiState.publicationsForDay, key = { it.id }) { pub ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate700)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(AmberWarningLight)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pub.platformSpecificTitle.ifBlank { "Post for ${pub.destination.displayName}" },
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate100
                                )
                                Text(
                                    text = "${pub.destination.displayName} • Due at ${formatTime(pub.plannedPublicationUtc)} • ${pub.publicationStatus.displayName}",
                                    style = Typography.bodyMedium,
                                    color = Slate400
                                )
                            }
                            Button(
                                onClick = { onNavigateToPublish(pub.id) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
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
                    style = Typography.labelLarge,
                    color = ElectricBluePrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.tasksForDay.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tasks scheduled for this day",
                                style = Typography.bodyLarge,
                                color = Slate400
                            )
                        }
                    }
                }
            } else {
                items(uiState.tasksForDay, key = { it.id }) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate800)
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
                                Text(
                                    text = task.title,
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate100
                                )
                                Text(
                                    text = "${formatTime(task.scheduledStartTimeUtc)} - ${formatTime(task.scheduledEndTimeUtc)} (${task.estimatedDurationMinutes}m) • ${task.status.displayName}",
                                    style = Typography.bodyMedium,
                                    color = Slate400
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

private fun formatTime(utc: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = utc
    return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}
