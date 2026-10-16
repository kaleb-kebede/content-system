package com.example.creatorgrowthos.ui.screens.pipeline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.domain.model.WorkflowStage
import com.example.creatorgrowthos.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PipelineScreen(
    viewModel: PipelineViewModel,
    onNavigateToScript: (contentId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    // Error Snackbar
    uiState.validationErrorMessage?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Quality Gate Validation", color = CrimsonCriticalLight, fontWeight = FontWeight.Bold) },
            text = { Text(error, color = Slate200) },
            confirmButton = {
                Button(onClick = { viewModel.clearError() }) {
                    Text("Understood")
                }
            },
            containerColor = Slate900
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
    ) {
        // Pipeline Header & Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PRODUCTION PIPELINE",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Content Flow",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            // View toggle (Kanban vs List)
            IconButton(
                onClick = { viewModel.toggleViewMode() },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate800)
            ) {
                Icon(
                    imageVector = if (uiState.isKanbanView) Icons.Default.ViewList else Icons.Default.ViewKanban,
                    contentDescription = "Toggle View",
                    tint = ElectricBluePrimary
                )
            }
        }

        // Stage Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedStageFilter == null,
                    onClick = { viewModel.setStageFilter(null) },
                    label = { Text("All Stages (${uiState.items.size})") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBluePrimary,
                        selectedLabelColor = Slate950,
                        containerColor = Slate800,
                        labelColor = Slate300
                    )
                )
            }
            items(WorkflowStage.values().filter { !it.isTerminal() }) { stage ->
                FilterChip(
                    selected = uiState.selectedStageFilter == stage,
                    onClick = { viewModel.setStageFilter(stage) },
                    label = { Text(stage.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBluePrimary,
                        selectedLabelColor = Slate950,
                        containerColor = Slate800,
                        labelColor = Slate300
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.isKanbanView) {
            // Kanban Column View
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val stagesToShow = listOf(
                    WorkflowStage.PLANNED,
                    WorkflowStage.RESEARCH,
                    WorkflowStage.SCRIPTING,
                    WorkflowStage.READY_TO_RECORD,
                    WorkflowStage.RECORDING,
                    WorkflowStage.EDITING,
                    WorkflowStage.QUALITY_REVIEW,
                    WorkflowStage.APPROVED,
                    WorkflowStage.SCHEDULED
                )

                stagesToShow.forEach { stage ->
                    val stageItems = uiState.items.filter { it.status == stage }
                    KanbanColumn(
                        stage = stage,
                        items = stageItems,
                        onMoveForward = { item ->
                            val nextStage = getNextStage(stage)
                            if (nextStage != null) {
                                viewModel.transitionItemStage(item.id, nextStage)
                            }
                        },
                        onOpenScript = { item -> onNavigateToScript(item.id) },
                        onDelete = { item -> viewModel.deleteItem(item.id) }
                    )
                }
            }
        } else {
            // Linear List View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    PipelineCard(
                        item = item,
                        onAdvanceStage = {
                            val next = getNextStage(item.status)
                            if (next != null) viewModel.transitionItemStage(item.id, next)
                        },
                        onOpenScript = { onNavigateToScript(item.id) },
                        onDelete = { viewModel.deleteItem(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun KanbanColumn(
    stage: WorkflowStage,
    items: List<ContentItemEntity>,
    onMoveForward: (ContentItemEntity) -> Unit,
    onOpenScript: (ContentItemEntity) -> Unit,
    onDelete: (ContentItemEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Slate800)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Column Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stage.displayName,
                    style = Typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Slate800
                ) {
                    Text(
                        text = "${items.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = Typography.labelSmall,
                        color = ElectricBluePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    PipelineCard(
                        item = item,
                        onAdvanceStage = { onMoveForward(item) },
                        onOpenScript = { onOpenScript(item) },
                        onDelete = { onDelete(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun PipelineCard(
    item: ContentItemEntity,
    onAdvanceStage: () -> Unit,
    onOpenScript: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate800),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate700)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ElectricBluePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = item.contentType.displayName,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = Typography.labelSmall,
                        fontSize = 10.sp,
                        color = ElectricBluePrimary
                    )
                }

                Text(
                    text = "Score: ${item.demandScore}/10",
                    style = Typography.labelSmall,
                    color = if (item.demandScore >= 7) MintProgressLight else Slate400
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.title,
                style = Typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Slate100
            )

            if (item.nextAction.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Next: ${item.nextAction}",
                    style = Typography.bodyMedium,
                    color = Slate400
                )
            }

            if (item.targetPlatforms.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    item.targetPlatforms.forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = Slate700
                        ) {
                            Text(
                                text = p.take(6),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                style = Typography.labelSmall,
                                fontSize = 9.sp,
                                color = Slate200
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onOpenScript) {
                    Icon(Icons.Default.EditNote, contentDescription = "Script", tint = ElectricBluePrimary)
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate600)
                }

                Button(
                    onClick = onAdvanceStage,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
                ) {
                    Text("Next Stage", color = Slate950, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun getNextStage(current: WorkflowStage): WorkflowStage? {
    return when (current) {
        WorkflowStage.IDEA -> WorkflowStage.VALIDATION
        WorkflowStage.VALIDATION -> WorkflowStage.PLANNED
        WorkflowStage.PLANNED -> WorkflowStage.RESEARCH
        WorkflowStage.RESEARCH -> WorkflowStage.SCRIPTING
        WorkflowStage.SCRIPTING -> WorkflowStage.READY_TO_RECORD
        WorkflowStage.READY_TO_RECORD -> WorkflowStage.RECORDING
        WorkflowStage.RECORDING -> WorkflowStage.EDITING
        WorkflowStage.EDITING -> WorkflowStage.QUALITY_REVIEW
        WorkflowStage.QUALITY_REVIEW -> WorkflowStage.APPROVED
        WorkflowStage.APPROVED -> WorkflowStage.SCHEDULED
        WorkflowStage.SCHEDULED -> null // Published handled via confirmed post
        else -> null
    }
}

val Slate300 = Color(0xFFCBD5E1)
