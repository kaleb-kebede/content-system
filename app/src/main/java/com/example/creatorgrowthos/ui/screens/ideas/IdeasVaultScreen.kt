package com.example.creatorgrowthos.ui.screens.ideas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeasVaultScreen(
    viewModel: IdeasViewModel,
    onNavigateToPipeline: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showCaptureDialog by remember { mutableStateOf(false) }

    if (showCaptureDialog) {
        QuickIdeaCaptureDialog(
            onDismiss = { showCaptureDialog = false },
            onSave = { title, raw, score, format ->
                val idea = ContentIdeaEntity(
                    id = java.util.UUID.randomUUID().toString(),
                    title = title,
                    rawIdea = raw,
                    demandScore = score,
                    format = format
                )
                viewModel.saveIdea(idea)
                showCaptureDialog = false
            }
        )
    }

    // Success Promotion Snackbar/Notice
    uiState.newlyPromotedContentId?.let { contentId ->
        AlertDialog(
            onDismissRequest = { viewModel.clearPromotedId() },
            title = { Text("Idea Promoted!", color = MintProgressLight, fontWeight = FontWeight.Bold) },
            text = { Text("Idea has been converted into an active production item in your pipeline.", color = Slate200) },
            confirmButton = {
                Button(onClick = {
                    viewModel.clearPromotedId()
                    onNavigateToPipeline()
                }) {
                    Text("Go to Pipeline")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.clearPromotedId() }) {
                    Text("Stay in Ideas")
                }
            },
            containerColor = Slate900
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp)
    ) {
        // Ideas Header
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "CONTENT IDEAS VAULT",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Ideas & Inspiration",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            Button(
                onClick = { showCaptureDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Slate950)
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Idea", color = Slate950, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search ideas, hooks, problems...", color = Slate600) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBluePrimary,
                unfocusedBorderColor = Slate800,
                focusedTextColor = Slate100,
                unfocusedTextColor = Slate100
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Demand Score Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Min Demand Score: ${uiState.minDemandFilter}/10",
                style = Typography.labelSmall,
                color = Slate400
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(1, 5, 7, 9).forEach { score ->
                    FilterChip(
                        selected = uiState.minDemandFilter == score,
                        onClick = { viewModel.setMinDemandFilter(score) },
                        label = { Text("$score+") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBluePrimary,
                            selectedLabelColor = Slate950
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Ideas List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.ideas, key = { it.id }) { idea ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (idea.demandScore >= 8) MintProgressLight.copy(alpha = 0.2f) else Slate800
                            ) {
                                Text(
                                    text = "Demand: ${idea.demandScore}/10",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = Typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (idea.demandScore >= 8) MintProgressLight else ElectricBluePrimary
                                )
                            }

                            if (idea.status == "PROMOTED") {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Slate800
                                ) {
                                    Text(
                                        text = "IN PIPELINE",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = Typography.labelSmall,
                                        color = MintProgressLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = idea.title,
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )

                        if (idea.rawIdea.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = idea.rawIdea,
                                style = Typography.bodyMedium,
                                color = Slate400
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.deleteIdea(idea.id) }) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate600)
                            }

                            if (idea.status != "PROMOTED") {
                                Button(
                                    onClick = { viewModel.promoteIdeaToPipeline(idea.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text("Promote to Pipeline", color = Slate950, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun QuickIdeaCaptureDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, rawIdea: String, demandScore: Int, format: ContentFormat) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var rawIdea by remember { mutableStateOf("") }
    var demandScore by remember { mutableStateOf(7) }
    var format by remember { mutableStateOf(ContentFormat.SHORT_FORM_VIDEO) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Capture New Content Idea", style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Idea Headline / Hook") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = rawIdea,
                    onValueChange = { rawIdea = it },
                    label = { Text("Rough thoughts / Problem / Angle") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(8.dp)
                )

                Text("Demand Score (1 to 10): $demandScore", style = Typography.labelSmall, color = Slate300)
                Slider(
                    value = demandScore.toFloat(),
                    onValueChange = { demandScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = format == ContentFormat.SHORT_FORM_VIDEO,
                        onClick = { format = ContentFormat.SHORT_FORM_VIDEO },
                        label = { Text("Short-form") }
                    )
                    FilterChip(
                        selected = format == ContentFormat.LONG_FORM_VIDEO,
                        onClick = { format = ContentFormat.LONG_FORM_VIDEO },
                        label = { Text("Long-form YT") }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onSave(title, rawIdea, demandScore, format)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Save to Vault", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}
