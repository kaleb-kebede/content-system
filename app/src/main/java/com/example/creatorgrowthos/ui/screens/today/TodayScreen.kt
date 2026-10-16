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
            Spacer(modifier = Modifier.height(10.dp))
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
                        letterSpacing = 1.6.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
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
                    color = Slate850,
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            progress = { uiState.completionPercentage / 100f },
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.5.dp,
                            color = if (uiState.completionPercentage >= 80f) MintProgressLight else ElectricBluePrimary,
                            trackColor = Slate750
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${uiState.completionPercentage.toInt()}%",
                            style = Typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.completionPercentage >= 80f) MintProgressLight else ElectricBluePrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Done",
                            style = Typography.bodySmall,
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
                    colors = CardDefaults.cardColors(containerColor = CrimsonCritical.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, CrimsonCriticalLight.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = CrimsonCritical.copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Critical Warning",
                                        tint = CrimsonCriticalLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${uiState.criticalMissedTasks.size} Critical Task(s) Overdue",
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonCriticalLight
                                )
                                Text(
                                    text = "Strict routine protocol active",
                                    style = Typography.labelSmall,
                                    color = CrimsonCriticalLight.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "A scheduled critical block was missed. You must reschedule, split, or record a reason before proceeding.",
                            style = Typography.bodyMedium,
                            color = Slate200
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        uiState.criticalMissedTasks.forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate900.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, CrimsonCritical.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate100
                                        )
                                        Text(
                                            text = "${task.estimatedDurationMinutes}m • ${task.priority.label}",
                                            style = Typography.bodySmall,
                                            color = Slate400
                                        )
                                    }
                                    Button(
                                        onClick = { viewModel.openRecoveryDialog(task) },
                                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonCritical),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text("Resolve", style = Typography.labelLarge, color = Slate100, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Current Scheduled Task & Primary Action (Start / Resume / Complete)
        item {
            val isActive = uiState.activeTask != null
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(
                    1.dp,
                    if (isActive) ElectricBluePrimary else BorderDefault
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isActive) ActiveSessionGradient else HeroCardGradient)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isActive) MintProgress.copy(alpha = 0.2f) else Slate800,
                            border = BorderStroke(1.dp, if (isActive) MintProgressLight.copy(alpha = 0.4f) else BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(MintProgressLight)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (isActive) "ACTIVE SESSION" else "UP NEXT",
                                    style = Typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isActive) MintProgressLight else Slate400
                                )
                            }
                        }

                        if (isActive) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MintProgress.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "In Progress",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = Typography.labelSmall,
                                    color = MintProgressLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val displayTask = uiState.activeTask ?: uiState.nextTask
                    if (displayTask != null) {
                        Text(
                            text = displayTask.title,
                            style = Typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Slate800
                            ) {
                                Text(
                                    text = "${formatTime(displayTask.scheduledStartTimeUtc)} - ${formatTime(displayTask.scheduledEndTimeUtc)}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = Typography.labelSmall,
                                    color = Slate300
                                )
                            }
                            Text(
                                text = "• ${displayTask.estimatedDurationMinutes} min session",
                                style = Typography.bodyMedium,
                                color = Slate400
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (displayTask.status == TaskStatus.ACTIVE) {
                                Button(
                                    onClick = { viewModel.completeTask(displayTask.id, displayTask.estimatedDurationMinutes) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Slate950)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Complete Session", color = Slate950, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { viewModel.pauseTask(displayTask.id) },
                                    modifier = Modifier.height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate200),
                                    border = BorderStroke(1.dp, Slate700)
                                ) {
                                    Icon(imageVector = Icons.Default.Pause, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pause")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.startTask(displayTask.id) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Slate950)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (displayTask.status == TaskStatus.PAUSED) "Resume Session" else "Start Session",
                                        color = Slate950,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = { viewModel.openRecoveryDialog(displayTask) },
                                    modifier = Modifier.height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                                    border = BorderStroke(1.dp, BorderDefault)
                                ) {
                                    Text("Adjust")
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "All scheduled sessions for today completed",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MintProgressLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Great job! You've stayed on routine. Review content or capture fresh ideas below.",
                            style = Typography.bodyMedium,
                            color = Slate400
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
                    colors = CardDefaults.cardColors(containerColor = AmberWarning.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, AmberWarningLight.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AmberWarning.copy(alpha = 0.25f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = AmberWarningLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Awaiting Publication Confirmation",
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100
                                )
                                Text(
                                    text = "Confirm platform publication to start 48h analytics window",
                                    style = Typography.bodySmall,
                                    color = Slate400
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        uiState.postsAwaitingConfirmation.forEach { pub ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Slate850,
                                border = BorderStroke(1.dp, BorderSubtle),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = pub.platformSpecificTitle.ifBlank { "Post for ${pub.destination.displayName}" },
                                            style = Typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate100
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Slate800
                                        ) {
                                            Text(
                                                text = pub.destination.displayName,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                style = Typography.labelSmall,
                                                color = ElectricBluePrimary
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onNavigateToPublish(pub.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarningLight),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                                    ) {
                                        Text("Confirm", color = Slate950, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Weekly Target Progress Card
        uiState.weeklyProgress?.let { summary ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, BorderDefault)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "WEEKLY PUBLISHING DISCIPLINE",
                                    style = Typography.labelSmall,
                                    color = ElectricBluePrimary,
                                    letterSpacing = 1.2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Target Progress",
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MintProgress.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${summary.totalConfirmed}/${summary.totalTarget} Published",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    style = Typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MintProgressLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { summary.overallPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MintProgressLight,
                            trackColor = Slate800,
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            summary.platformBreakdown.forEach { p ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Slate800,
                                    border = BorderStroke(1.dp, BorderSubtle),
                                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = p.platform.shortName, style = Typography.labelSmall, color = Slate400)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${p.confirmedPublished}/${p.weeklyTarget}",
                                            style = Typography.titleMedium,
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
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate800
                ) {
                    Text(
                        text = "${uiState.todayTasks.size} Sessions",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = Typography.labelSmall,
                        color = Slate400
                    )
                }
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
            Spacer(modifier = Modifier.height(32.dp))
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
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (task.status == TaskStatus.ACTIVE) ElectricBluePrimary else BorderSubtle
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connected Timeline Status Node
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(statusColor)
                    .border(
                        2.dp,
                        if (task.status == TaskStatus.ACTIVE) ElectricBluePrimary.copy(alpha = 0.5f) else Color.Transparent,
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(14.dp))

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
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                style = Typography.labelSmall,
                                fontSize = 9.sp,
                                color = CrimsonCriticalLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${formatTime(task.scheduledStartTimeUtc)} - ${formatTime(task.scheduledEndTimeUtc)}",
                        style = Typography.bodySmall,
                        color = Slate400
                    )
                    Text(
                        text = " • ",
                        style = Typography.bodySmall,
                        color = Slate600
                    )
                    Text(
                        text = task.status.displayName,
                        style = Typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Context Actions based on status
            when (task.status) {
                TaskStatus.UPCOMING -> {
                    IconButton(
                        onClick = onStart,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start", tint = ElectricBluePrimary)
                    }
                }
                TaskStatus.ACTIVE -> {
                    IconButton(
                        onClick = onComplete,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = MintProgressLight)
                    }
                }
                TaskStatus.MISSED, TaskStatus.OVERDUE -> {
                    Button(
                        onClick = onRecovery,
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonCritical),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("Decision", style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
                TaskStatus.COMPLETED -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MintProgress,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                else -> {
                    IconButton(
                        onClick = onRecovery,
                        modifier = Modifier.size(44.dp)
                    ) {
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

