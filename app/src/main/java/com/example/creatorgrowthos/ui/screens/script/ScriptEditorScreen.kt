package com.example.creatorgrowthos.ui.screens.script

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.ScriptSectionEntity
import com.example.creatorgrowthos.theme.*

@Composable
fun ScriptEditorScreen(
    scriptId: String,
    contentItemId: String?,
    viewModel: ScriptViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(scriptId) {
        viewModel.loadScript(scriptId, contentItemId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Editor Header & Word Count / Duration Pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCRIPT WRITING HUB",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.currentScript?.title ?: "Script Editor",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Slate800,
                border = BorderStroke(1.dp, Slate700)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = ElectricBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${uiState.estimatedDurationFormatted} (${uiState.wordCount} words)",
                        style = Typography.labelSmall,
                        color = Slate100,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (uiState.isSaved) Icons.Default.CloudDone else Icons.Default.Sync,
                        contentDescription = "Save status",
                        tint = if (uiState.isSaved) MintProgressLight else AmberWarningLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Full Script Body / Scene Breakdown
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Main Body Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate850),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Full Script Narrative & Teleprompter",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate100
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.currentScript?.scriptBody ?: "",
                            onValueChange = { viewModel.updateScriptBody(it) },
                            placeholder = { Text("Write your full script here... Word count and duration update automatically.", color = Slate600) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 160.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBluePrimary,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            )
                        )
                    }
                }
            }

            // Structured Scenes
            if (uiState.sections.isNotEmpty()) {
                item {
                    Text(
                        text = "Scene Progression & Visual Beats (${uiState.sections.size} beats)",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                }

                items(uiState.sections, key = { it.id }) { section ->
                    SceneSectionCard(
                        section = section,
                        onUpdate = { newWords, visualNotes ->
                            viewModel.updateSection(section.id, newWords, visualNotes)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }
}

@Composable
fun SceneSectionCard(
    section: ScriptSectionEntity,
    onUpdate: (spokenWords: String, visualNotes: String) -> Unit
) {
    var words by remember(section.spokenWords) { mutableStateOf(section.spokenWords) }
    var visuals by remember(section.visualNotes) { mutableStateOf(section.visualNotes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate850),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Slate800)
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
                        text = "BEAT ${section.sectionOrder + 1}: ${section.title}",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = Typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBluePrimary
                    )
                }

                Text(
                    text = "~${section.durationEstimateSeconds}s",
                    style = Typography.labelSmall,
                    color = Slate400
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Spoken Words
            OutlinedTextField(
                value = words,
                onValueChange = {
                    words = it
                    onUpdate(words, visuals)
                },
                label = { Text("Spoken Voice-Over", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBluePrimary,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Visual / On-Screen Notes
            OutlinedTextField(
                value = visuals,
                onValueChange = {
                    visuals = it
                    onUpdate(words, visuals)
                },
                label = { Text("B-Roll / Visual Notes / Text On-Screen", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MintProgressLight,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Slate200,
                    unfocusedTextColor = Slate200
                )
            )
        }
    }
}
