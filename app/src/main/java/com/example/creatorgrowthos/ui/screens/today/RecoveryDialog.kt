package com.example.creatorgrowthos.ui.screens.today

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.creatorgrowthos.data.local.entity.ContentTaskEntity
import com.example.creatorgrowthos.domain.scheduler.RecoveryOption
import com.example.creatorgrowthos.theme.*

@Composable
fun RecoveryDialog(
    task: ContentTaskEntity,
    options: List<RecoveryOption>,
    onReschedule: (newStart: Long, newEnd: Long, reason: String) -> Unit,
    onSplit: (part1Min: Int, part2Min: Int, part2Start: Long) -> Unit,
    onBlock: (reason: String) -> Unit,
    onSkip: (reason: String) -> Unit,
    onEmergencyOverride: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf("Prior task ran overtime") }
    var customReason by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = BorderStroke(1.dp, BorderDefault),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Warning Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CrimsonCritical.copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = CrimsonCriticalLight,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "Critical Task Missed",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Text(
                            text = "Strict routine decision required",
                            style = Typography.bodySmall,
                            color = CrimsonCriticalLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Task Details Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Slate850,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = task.title,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${task.estimatedDurationMinutes}m session",
                                style = Typography.bodySmall,
                                color = Slate300
                            )
                            Text(text = " • ", style = Typography.bodySmall, color = Slate600)
                            Text(
                                text = task.priority.label,
                                style = Typography.labelSmall,
                                color = AmberWarningLight
                            )
                        }
                        if (task.rescheduleCount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Previously rescheduled ${task.rescheduleCount} time(s)",
                                style = Typography.labelSmall,
                                color = CrimsonCriticalLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "SELECT RECOVERY ACTION",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Decision Options
                options.forEach { option ->
                    val (icon, iconTint, iconBg) = when (option.actionType) {
                        "RESCHEDULE" -> Triple(Icons.Default.Alarm, ElectricBluePrimary, ElectricBluePrimary.copy(alpha = 0.15f))
                        "SPLIT" -> Triple(Icons.Default.CallSplit, PurpleAccentLight, PurpleAccent.copy(alpha = 0.15f))
                        "MARK_BLOCKED" -> Triple(Icons.Default.Block, AmberWarningLight, AmberWarning.copy(alpha = 0.15f))
                        else -> Triple(Icons.Default.SkipNext, Slate400, Slate700.copy(alpha = 0.4f))
                    }

                    Surface(
                        onClick = {
                            val reason = if (showCustomInput && customReason.isNotBlank()) customReason else selectedReason
                            when (option.actionType) {
                                "RESCHEDULE" -> {
                                    val start = option.suggestedStartUtc ?: (System.currentTimeMillis() + 15 * 60 * 1000L)
                                    val end = option.suggestedEndUtc ?: (start + task.estimatedDurationMinutes * 60 * 1000L)
                                    onReschedule(start, end, reason)
                                }
                                "SPLIT" -> {
                                    val half = task.estimatedDurationMinutes / 2
                                    val start = option.suggestedStartUtc ?: (System.currentTimeMillis() + 15 * 60 * 1000L)
                                    onSplit(half, half, start)
                                }
                                "MARK_BLOCKED" -> onBlock(reason)
                                "SKIP" -> onSkip(reason)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        color = Slate850,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = iconBg,
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    style = Typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate100
                                )
                                Text(
                                    text = option.description,
                                    style = Typography.bodySmall,
                                    color = Slate400
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Slate500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Document Reason
                Text(
                    text = "DOCUMENT REASON (AUDIT LOG)",
                    style = Typography.labelSmall,
                    color = Slate400,
                    letterSpacing = 1.0.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customReason,
                    onValueChange = {
                        customReason = it
                        showCustomInput = true
                    },
                    placeholder = { Text("e.g. Script revisions took longer than estimated", color = Slate500) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = BorderDefault,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100,
                        focusedContainerColor = Slate850,
                        unfocusedContainerColor = Slate850
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Emergency Override Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Decide Later", color = Slate400, style = Typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = onEmergencyOverride,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarningLight),
                        border = BorderStroke(1.dp, AmberWarningLight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Emergency Override", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

