package com.example.creatorgrowthos.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.theme.*

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.settings

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "SYSTEM CONFIGURATION",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Settings & Routine",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }
        }

        // Routine Engine Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Schedule Enforcement: ${settings.scheduleEnforcementMode}",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "STRICT enforcement requires explicit decisions on critical missed tasks.",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Working Hours: %02d:%02d - %02d:%02d".format(
                            settings.dailyRoutineStartHour, settings.dailyRoutineStartMinute,
                            settings.dailyRoutineEndHour, settings.dailyRoutineEndMinute
                        ),
                        style = Typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate200
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Buffer Time Between Tasks: ${settings.bufferTimeBetweenTasksMinutes} minutes",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Narration Reading Speed: ${settings.narrationWordsPerMinute} Words/Min",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notification Settings",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Morning Agenda Reminder", style = Typography.bodyLarge, color = Slate200)
                        Switch(
                            checked = settings.morningAgendaReminderEnabled,
                            onCheckedChange = {
                                viewModel.updateSettings(settings.copy(morningAgendaReminderEnabled = it))
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Critical Missed Task Alerts", style = Typography.bodyLarge, color = Slate200)
                        Switch(
                            checked = settings.criticalMissedTaskAlertsEnabled,
                            onCheckedChange = {
                                viewModel.updateSettings(settings.copy(criticalMissedTaskAlertsEnabled = it))
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Publishing Due Notifications", style = Typography.bodyLarge, color = Slate200)
                        Switch(
                            checked = settings.publishingReminderEnabled,
                            onCheckedChange = {
                                viewModel.updateSettings(settings.copy(publishingReminderEnabled = it))
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
