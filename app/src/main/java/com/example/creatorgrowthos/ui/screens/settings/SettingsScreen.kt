package com.example.creatorgrowthos.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SYSTEM ARCHITECTURE",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.4.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Settings & Routine",
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MintSuccess.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MintSuccess.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MintProgress)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ENGINE ONLINE",
                            style = Typography.labelSmall,
                            color = MintProgress,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }
            }
        }

        // Routine Engine Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ElectricBluePrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Routine Engine",
                                    tint = ElectricBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ROUTINE ENGINE",
                                style = Typography.labelSmall,
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                            Text(
                                text = "Schedule Enforcement Rules",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Enforcement Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isStrict = settings.scheduleEnforcementMode == "STRICT"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateSettings(settings.copy(scheduleEnforcementMode = "STRICT"))
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isStrict) AmberWarning.copy(alpha = 0.14f) else Slate800,
                            border = BorderStroke(
                                1.dp,
                                if (isStrict) AmberWarning else BorderSubtle
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "STRICT MODE",
                                    style = Typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStrict) AmberWarning else Slate400
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Non-negotiable",
                                    style = Typography.bodySmall,
                                    color = if (isStrict) Slate200 else Slate500
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.updateSettings(settings.copy(scheduleEnforcementMode = "FLEXIBLE"))
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (!isStrict) ElectricBluePrimary.copy(alpha = 0.14f) else Slate800,
                            border = BorderStroke(
                                1.dp,
                                if (!isStrict) ElectricBluePrimary else BorderSubtle
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "FLEXIBLE MODE",
                                    style = Typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isStrict) ElectricBluePrimary else Slate400
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Advisory only",
                                    style = Typography.bodySmall,
                                    color = if (!isStrict) Slate200 else Slate500
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (settings.scheduleEnforcementMode == "STRICT") {
                            "Requires explicit recovery resolution (Complete Now, Reschedule, or Cancel with reason) on critical missed sessions before proceeding."
                        } else {
                            "Missed sessions are highlighted on the agenda but will not block future workflow transitions."
                        },
                        style = Typography.bodySmall,
                        color = Slate400,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Working Hours Display
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Production Window",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate200
                            )
                            Text(
                                text = "Dedicated creation routine hours",
                                style = Typography.bodySmall,
                                color = Slate400
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Slate800,
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "%02d:%02d - %02d:%02d".format(
                                    settings.dailyRoutineStartHour, settings.dailyRoutineStartMinute,
                                    settings.dailyRoutineEndHour, settings.dailyRoutineEndMinute
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlueLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buffer Time Between Tasks
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Task Transition Buffer",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate200
                            )
                            Text(
                                text = "${settings.bufferTimeBetweenTasksMinutes} mins",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MintProgress
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30).forEach { mins ->
                                val isSelected = settings.bufferTimeBetweenTasksMinutes == mins
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updateSettings(settings.copy(bufferTimeBetweenTasksMinutes = mins))
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) ElectricBluePrimary.copy(alpha = 0.16f) else Slate800,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) ElectricBluePrimary else BorderSubtle
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${mins}m",
                                            style = Typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) ElectricBlueLight else Slate400
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reading Speed (WPM)
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Narration Reading Speed",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate200
                            )
                            Text(
                                text = "${settings.narrationWordsPerMinute} WPM",
                                style = Typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = PurpleLight
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(120, 140, 160, 180).forEach { wpm ->
                                val isSelected = settings.narrationWordsPerMinute == wpm
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            viewModel.updateSettings(settings.copy(narrationWordsPerMinute = wpm))
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) PurpleAccent.copy(alpha = 0.16f) else Slate800,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) PurpleAccent else BorderSubtle
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$wpm",
                                            style = Typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) PurpleLight else Slate400
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        val estimatedSeconds = ((350.0 / settings.narrationWordsPerMinute) * 60).toInt()
                        Text(
                            text = "Pacing benchmark: ~%d sec for a 350-word short-form video.".format(estimatedSeconds),
                            style = Typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            }
        }

        // Notification Preferences
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintProgress.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = MintProgress,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "NOTIFICATION ENGINE",
                                style = Typography.labelSmall,
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                            Text(
                                text = "Alerts & Briefings",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    SettingSwitchRow(
                        title = "Morning Agenda Reminder",
                        description = "07:00 AM daily briefing for prioritized tasks & publications",
                        icon = Icons.Default.Schedule,
                        iconTint = ElectricBluePrimary,
                        checked = settings.morningAgendaReminderEnabled,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(morningAgendaReminderEnabled = it))
                        }
                    )

                    HorizontalDivider(color = BorderSubtle, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    SettingSwitchRow(
                        title = "Critical Missed Task Alerts",
                        description = "Immediate notification when a critical deadline or session slips",
                        icon = Icons.Default.Warning,
                        iconTint = AmberWarning,
                        checked = settings.criticalMissedTaskAlertsEnabled,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(criticalMissedTaskAlertsEnabled = it))
                        }
                    )

                    HorizontalDivider(color = BorderSubtle, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                    SettingSwitchRow(
                        title = "Publishing Due Notifications",
                        description = "Assisted publishing trigger with clipboard caption packaging",
                        icon = Icons.Default.CloudUpload,
                        iconTint = MintProgress,
                        checked = settings.publishingReminderEnabled,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(publishingReminderEnabled = it))
                        }
                    )
                }
            }
        }

        // System Architecture & Local DB Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = PurpleAccent.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "System Spec",
                                    tint = PurpleAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ARCHITECTURE SPECIFICATION",
                                style = Typography.labelSmall,
                                color = Slate400,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp
                            )
                            Text(
                                text = "Local Storage & Engine",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Database Runtime", style = Typography.bodyMedium, color = Slate400)
                        Text(text = "SQLite / Room ORM", style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Slate200)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Storage Security", style = Typography.bodyMedium, color = Slate400)
                        Text(text = "100% On-Device Local", style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MintProgress)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "App Version", style = Typography.bodyMedium, color = Slate400)
                        Text(text = "v1.0.0-PRO (Native Jetpack Compose)", style = Typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = ElectricBlueLight)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconTint.copy(alpha = 0.12f),
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = Typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = Typography.bodySmall,
                    color = Slate400,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Slate950,
                checkedTrackColor = ElectricBluePrimary,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800,
                uncheckedBorderColor = BorderSubtle
            )
        )
    }
}
