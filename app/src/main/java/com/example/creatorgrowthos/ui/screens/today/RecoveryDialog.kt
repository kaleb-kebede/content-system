package com.example.creatorgrowthos.ui.screens.today

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
                .padding(8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Warning Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CrimsonCriticalLight.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = CrimsonCriticalLight
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Critical Task Missed",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        Text(
                            text = "A recovery decision is required",
                            style = Typography.bodyMedium,
                            color = CrimsonCriticalLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Task Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = task.title,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated: ${task.estimatedDurationMinutes}m • Priority: ${task.priority.label}",
                            style = Typography.bodyMedium,
                            color = Slate400
                        )
                        if (task.rescheduleCount > 0) {
                            Text(
                                text = "Previously rescheduled ${task.rescheduleCount} time(s)",
                                style = Typography.labelSmall,
                                color = AmberWarningLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Select Recovery Strategy:",
                    style = Typography.labelLarge,
                    color = Slate200
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Options
                options.forEach { option ->
                    val icon = when (option.actionType) {
                        "RESCHEDULE" -> Icons.Default.Alarm
                        "SPLIT" -> Icons.Default.CallSplit
                        "MARK_BLOCKED" -> Icons.Default.Block
                        else -> Icons.Default.SkipNext
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
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
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = ElectricBluePrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = option.title, style = Typography.bodyLarge, fontWeight = FontWeight.Medium, color = Slate100)
                                Text(text = option.description, style = Typography.bodyMedium, color = Slate400)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Document Reason
                Text(text = "Document Cause / Reason:", style = Typography.labelLarge, color = Slate400)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customReason,
                    onValueChange = {
                        customReason = it
                        showCustomInput = true
                    },
                    placeholder = { Text("e.g. Script revisions took longer than estimated", color = Slate600) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBluePrimary,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate100,
                        unfocusedTextColor = Slate100
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Emergency Override Action (Recorded)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Decide Later", color = Slate400)
                    }

                    OutlinedButton(
                        onClick = onEmergencyOverride,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarningLight),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AmberWarningLight))
                    ) {
                        Text("Emergency Override")
                    }
                }
            }
        }
    }
}
