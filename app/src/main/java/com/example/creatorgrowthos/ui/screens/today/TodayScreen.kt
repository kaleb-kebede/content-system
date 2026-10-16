package com.example.creatorgrowthos.ui.screens.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.domain.model.TaskPriority
import com.example.creatorgrowthos.domain.model.TaskStatus
import com.example.creatorgrowthos.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    viewModel: TodayViewModel,
    onNavigateToPublish: (publicationId: String) -> Unit,
    onNavigateToPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Recovery Dialog when a critical task requires decision
    if (uiState.showRecoveryDialog && uiState.selectedTaskForRecovery != null) {
        RecoveryDialog(
            task = uiState.selectedTaskForRecovery!!,
            options = uiState.recoveryOptions,
            onReschedule = { start, end, reason ->
                viewModel.rescheduleTask(uiState.selectedTaskForRecovery!!.id, start, end, reason)
            },
            onSplit = { p1, p2, start ->
                viewModel.rescheduleTask(uiState.selectedTaskForRecovery!!.id, start, start + p1 * 60 * 1000L, "Split task")
            },
            onBlock = { reason ->
                viewModel.blockTask(uiState.selectedTaskForRecovery!!.id, reason)
            },
            onSkip = { reason ->
                viewModel.skipTask(uiState.selectedTaskForRecovery!!.id, reason)
            },
            onEmergencyOverride = {
                viewModel.recordEmergencyOverride(uiState.selectedTaskForRecovery!!.id)
            },
            onDismiss = {
                viewModel.dismissRecoveryDialog()
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
        // 1. Date Header & Daily Focus
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DAILY COMMAND CENTER",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.currentDateFormatted.ifBlank { "Today's Agenda" },
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }

                // Completion Progress Ring / Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Slate800,
                    border = BorderStroke(1.dp, Slate700)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${uiState.completionPercentage.toInt()}%",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.completionPercentage >= 80f) MintProgressLight else ElectricBluePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Done",
                            style = Typography.bodyMedium,
                            color = Slate400
                        )
                    }
                }
            }
        }

        // 2. Critical Missed Tasks Decision Banner (Strict Routine Enforcement)
        if (uiState.criticalMissedTasks.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CrimsonCriticalLight.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, CrimsonCriticalLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Critical Warning",
                                tint = CrimsonCriticalLight,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${uiState.criticalMissedTasks.size} Critical Missed Task(s)",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonCriticalLight
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Strict enforcement is active. You must decide whether to reschedule, split, or justify skipping before next sessions.",
                            style = Typography.bodyMedium,
                            color = Slate200
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        uiState.criticalMissedTasks.forEach { task ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ${task.title}",
                                    style = Typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Slate100,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = { viewModel.openRecoveryDialog(task) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonCritical),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Resolve", style = Typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Current Scheduled Task & Primary Action (Start / Resume / Complete)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, if (uiState.activeTask != null) ElectricBluePrimary else Slate700)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (uiState.activeTask != null) MintProgressLight.copy(alpha = 0.2f) else Slate800
                        ) {
                            Text(
                                text = if (uiState.activeTask != null) "CURRENT ACTIVE SESSION" else "UP NEXT",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = Typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.activeTask != null) MintProgressLight else Slate400
                            )
                        }

                        if (uiState.activeTask != null) {
                            Text(
                                text = "In Progress",
                                style = Typography.labelLarge,
                                color = MintProgressLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val displayTask = uiState.activeTask ?: uiState.nextTask
                    if (displayTask != null) {
                        Text(
                            text = displayTask.title,
                            style = Typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Scheduled: ${formatTime(displayTask.scheduledStartTimeUtc)} - ${formatTime(displayTask.scheduledEndTimeUtc)} (${displayTask.estimatedDurationMinutes}m)",
                            style = Typography.bodyMedium,
                            color = Slate400
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (displayTask.status == TaskStatus.ACTIVE) {
                                Button(
                                    onClick = { viewModel.completeTask(displayTask.id, displayTask.estimatedDurationMinutes) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Complete")
                                }
                                OutlinedButton(
                                    onClick = { viewModel.pauseTask(displayTask.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200)
                                ) {
                                    Icon(imageVector = Icons.Default.Pause, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pause")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.startTask(displayTask.id) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Slate950)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (displayTask.status == TaskStatus.PAUSED) "Resume" else "Start Session",
                                        color = Slate950,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openRecoveryDialog(displayTask) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate400)
                                ) {
                                    Text("Adjust")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No active or upcoming sessions right now",
                            style = Typography.bodyLarge,
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "All scheduled items for today have been completed or addressed.",
                            style = Typography.bodyMedium,
                            color = Slate600
                        )
                    }
                }
            }
        }

        // 4. Posts Awaiting Publication Confirmation (Assisted Publishing Gate)
        if (uiState.postsAwaitingConfirmation.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AmberWarningLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = AmberWarningLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Awaiting Publication Confirmation",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Did you complete publishing on the platform? Confirm and save URL:",
                            style = Typography.bodyMedium,
                            color = Slate400
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        uiState.postsAwaitingConfirmation.forEach { pub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = pub.platformSpecificTitle.ifBlank { "Post for ${pub.destination.displayName}" },
                                        style = Typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate100
                                    )
                                    Text(
                                        text = pub.destination.displayName,
                                        style = Typography.labelSmall,
                                        color = ElectricBluePrimary
                                    )
                                }
                                Button(
                                    onClick = { onNavigateToPublish(pub.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberWarningLight),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Confirm", color = Slate950, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Weekly Target Progress Card (Section 9)
        uiState.weeklyProgress?.let { summary ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Slate700)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Weekly Publishing Progress",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                            Text(
                                text = "${summary.totalConfirmed}/${summary.totalTarget} Published",
                                style = Typography.labelLarge,
                                color = MintProgressLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { summary.overallPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MintProgressLight,
                            trackColor = Slate800,
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            summary.platformBreakdown.forEach { p ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = p.platform.shortName, style = Typography.labelSmall, color = Slate400)
                                    Text(
                                        text = "${p.confirmedPublished}/${p.weeklyTarget}",
                                        style = Typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (p.confirmedPublished >= p.weeklyTarget) MintProgressLight else Slate100
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Chronological Today's Timeline Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Schedule Timeline",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Text(
                    text = "${uiState.todayTasks.size} Tasks",
                    style = Typography.labelSmall,
                    color = Slate400
                )
            }
        }

        // 7. Chronological Task Timeline Items
        items(uiState.todayTasks, key = { it.id }) { task ->
            TaskTimelineItem(
                task = task,
                onStart = { viewModel.startTask(task.id) },
                onComplete = { viewModel.completeTask(task.id, task.estimatedDurationMinutes) },
                onRecovery = { viewModel.openRecoveryDialog(task) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TaskTimelineItem(
    task: ContentTaskEntity,
    onStart: () -> Unit,
    onComplete: () -> Unit,
    onRecovery: () -> Unit
) {
    val statusColor = when (task.status) {
        TaskStatus.ACTIVE -> MintProgressLight
        TaskStatus.COMPLETED -> MintProgress
        TaskStatus.MISSED -> CrimsonCriticalLight
        TaskStatus.OVERDUE -> CrimsonCriticalLight
        TaskStatus.PAUSED -> AmberWarningLight
        TaskStatus.BLOCKED -> AmberWarning
        TaskStatus.SKIPPED -> Slate600
        TaskStatus.UPCOMING -> ElectricBluePrimary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (task.status == TaskStatus.ACTIVE) ElectricBluePrimary else Slate800)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timeline Status Indicator
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.status == TaskStatus.COMPLETED) Slate400 else Slate100
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
                    text = "${formatTime(task.scheduledStartTimeUtc)} - ${formatTime(task.scheduledEndTimeUtc)} • ${task.status.displayName}",
                    style = Typography.bodyMedium,
                    color = statusColor
                )
            }

            // Context Actions based on status
            when (task.status) {
                TaskStatus.UPCOMING -> {
                    IconButton(onClick = onStart) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = ElectricBluePrimary)
                    }
                }
                TaskStatus.ACTIVE -> {
                    IconButton(onClick = onComplete) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = MintProgressLight)
                    }
                }
                TaskStatus.MISSED, TaskStatus.OVERDUE -> {
                    Button(
                        onClick = onRecovery,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonCritical),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Decision", style = Typography.labelSmall)
                    }
                }
                TaskStatus.COMPLETED -> {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = MintProgress)
                }
                else -> {
                    IconButton(onClick = onRecovery) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Slate400)
                    }
                }
            }
        }
    }
}

private fun formatTime(utc: Long): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = utc
    return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
}
