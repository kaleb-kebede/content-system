package com.example.creatorgrowthos.ui.screens.goals

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.GoalEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.theme.*

@Composable
fun GoalsStrategyScreen(
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showEditDialogForAccount by remember { mutableStateOf<PlatformAccountEntity?>(null) }
    var showNewGoalDialog by remember { mutableStateOf(false) }

    if (showEditDialogForAccount != null) {
        EditAudienceDialog(
            account = showEditDialogForAccount!!,
            onDismiss = { showEditDialogForAccount = null },
            onSave = { count ->
                viewModel.updateAudience(showEditDialogForAccount!!.platform, count)
                showEditDialogForAccount = null
            }
        )
    }

    if (showNewGoalDialog) {
        CreateGoalDialog(
            onDismiss = { showNewGoalDialog = false },
            onSave = { goal ->
                viewModel.saveGoal(goal)
                showNewGoalDialog = false
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
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CREATOR STRATEGY & MILESTONES",
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "100K Audience Path",
                        style = Typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }

                Button(
                    onClick = { showNewGoalDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Slate950)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Goal", color = Slate950, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Platform 100K Target Progress Cards
        items(uiState.accounts, key = { it.id }) { acc ->
            val remaining = (acc.targetAudienceCount - acc.currentAudienceCount).coerceAtLeast(0)
            val progress = if (acc.targetAudienceCount > 0) {
                (acc.currentAudienceCount.toFloat() / acc.targetAudienceCount).coerceIn(0f, 1f)
            } else 0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = acc.platform.displayName,
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                            Text(
                                text = acc.accountHandle,
                                style = Typography.labelSmall,
                                color = ElectricBluePrimary
                            )
                        }

                        IconButton(onClick = { showEditDialogForAccount = acc }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit count", tint = Slate400)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Current Audience", style = Typography.labelSmall, color = Slate400)
                            Text(
                                text = "%,d".format(acc.currentAudienceCount),
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate100
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Target (100K Milestone)", style = Typography.labelSmall, color = Slate400)
                            Text(
                                text = "%,d".format(acc.targetAudienceCount),
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MintProgressLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MintProgressLight,
                        trackColor = Slate800
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${String.format("%.1f", progress * 100)}% Complete • ${"%,d".format(remaining)} to go",
                        style = Typography.labelSmall,
                        color = Slate400
                    )
                }
            }
        }

        // Content Pillars Section
        item {
            Text(
                text = "Content Pillars Distribution",
                style = Typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Slate100
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.pillars.forEach { pillar ->
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Slate850),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Slate800)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "${pillar.targetPercentage.toInt()}%",
                                style = Typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBluePrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = pillar.name,
                                style = Typography.labelSmall,
                                maxLines = 2,
                                color = Slate200
                            )
                        }
                    }
                }
            }
        }

        // Custom Strategy Goals
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Strategic Goals",
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Text(
                    text = "${uiState.goals.size} Goals",
                    style = Typography.labelSmall,
                    color = Slate400
                )
            }
        }

        items(uiState.goals, key = { it.id }) { goal ->
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
                    IconButton(onClick = { viewModel.toggleGoal(goal.id, !goal.isAchieved) }) {
                        Icon(
                            imageVector = if (goal.isAchieved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Toggle",
                            tint = if (goal.isAchieved) MintProgressLight else Slate600
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = goal.title,
                            style = Typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (goal.isAchieved) Slate400 else Slate100
                        )
                        Text(
                            text = "${goal.goalType} • Target: ${goal.targetMetric.toInt()} ${goal.unit}",
                            style = Typography.labelSmall,
                            color = Slate400
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun EditAudienceDialog(
    account: PlatformAccountEntity,
    onDismiss: () -> Unit,
    onSave: (count: Long) -> Unit
) {
    var countInput by remember { mutableStateOf(account.currentAudienceCount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update ${account.platform.displayName} Audience", style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter your current confirmed subscriber / follower count:", color = Slate300)
                OutlinedTextField(
                    value = countInput,
                    onValueChange = { countInput = it },
                    label = { Text("Current Count") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = countInput.toLongOrNull() ?: account.currentAudienceCount
                    onSave(count)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save Count", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}

@Composable
fun CreateGoalDialog(
    onDismiss: () -> Unit,
    onSave: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var goalType by remember { mutableStateOf("AUDIENCE_GROWTH") }
    var targetMetric by remember { mutableStateOf("100000") }
    var unit by remember { mutableStateOf("followers") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Strategic Goal", style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = targetMetric,
                    onValueChange = { targetMetric = it },
                    label = { Text("Target Metric") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (e.g. subscribers, views, dollars)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(
                            GoalEntity(
                                id = java.util.UUID.randomUUID().toString(),
                                title = title,
                                goalType = goalType,
                                targetMetric = targetMetric.toFloatOrNull() ?: 100f,
                                unit = unit
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save Goal", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}
