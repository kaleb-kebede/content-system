package com.example.creatorgrowthos.ui.screens.repurposing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepurposingScreen(
    viewModel: RepurposingViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var showCreateDialogForMaster by remember { mutableStateOf<ContentItemEntity?>(null) }

    if (showCreateDialogForMaster != null) {
        CreateDerivativeDialog(
            master = showCreateDialogForMaster!!,
            onDismiss = { showCreateDialogForMaster = null },
            onSave = { destination, title, hook, caption, hashtags ->
                viewModel.createDerivative(
                    masterId = showCreateDialogForMaster!!.id,
                    destination = destination,
                    title = title,
                    hook = hook,
                    caption = caption,
                    hashtags = hashtags,
                    plannedPublicationUtc = System.currentTimeMillis() + 86400000L * 2
                )
                showCreateDialogForMaster = null
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
            Column {
                Text(
                    text = "1-TO-MANY REPURPOSING HUB",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Content Derivatives",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transform 1 master idea or YouTube long-form video into multiple short-form derivatives for YouTube Shorts, TikTok, and Instagram Reels.",
                    style = Typography.bodyMedium,
                    color = Slate400
                )
            }
        }

        items(uiState.masterItems, key = { it.id }) { master ->
            val derivatives = uiState.allDerivatives.filter { it.masterContentId == master.id }

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
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ElectricBluePrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "MASTER CONTENT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = Typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBluePrimary
                            )
                        }

                        Button(
                            onClick = { showCreateDialogForMaster = master },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Slate950)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Derive", color = Slate950, style = Typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = master.title,
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Text(
                        text = "Format: ${master.contentType.displayName} • Stage: ${master.status.displayName}",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (derivatives.isEmpty()) {
                        Text(
                            text = "No derivatives created yet. Tap '+ Derive' to generate platform clips.",
                            style = Typography.bodyMedium,
                            color = Slate600
                        )
                    } else {
                        Text(
                            text = "Derivatives (${derivatives.size}):",
                            style = Typography.labelLarge,
                            color = Slate300
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        derivatives.forEach { deriv ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Slate800),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = deriv.title,
                                            style = Typography.bodyLarge,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate100
                                        )
                                        Text(
                                            text = "${deriv.destinationPlatform.displayName} • Hook: \"${deriv.specificHook.take(40)}\"",
                                            style = Typography.bodyMedium,
                                            color = ElectricBluePrimary
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteDerivative(deriv.id) }) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate600)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

@Composable
fun CreateDerivativeDialog(
    master: ContentItemEntity,
    onDismiss: () -> Unit,
    onSave: (destination: PlatformType, title: String, hook: String, caption: String, hashtags: List<String>) -> Unit
) {
    var destination by remember { mutableStateOf(PlatformType.YOUTUBE_SHORTS) }
    var title by remember { mutableStateOf("${master.title} - Clip") }
    var hook by remember { mutableStateOf("") }
    var caption by remember { mutableStateOf(master.title) }
    var hashtagsInput by remember { mutableStateOf("creator,growth,buildinpublic") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Platform Derivative", style = Typography.titleMedium, fontWeight = FontWeight.Bold, color = Slate100) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Select Target Platform:", style = Typography.labelSmall, color = Slate400)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(PlatformType.YOUTUBE_SHORTS, PlatformType.TIKTOK, PlatformType.INSTAGRAM_REELS).forEach { p ->
                        FilterChip(
                            selected = destination == p,
                            onClick = { destination = p },
                            label = { Text(p.shortName, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Derivative Title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = hook,
                    onValueChange = { hook = it },
                    label = { Text("Platform-specific Hook (0-3s)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = hashtagsInput,
                    onValueChange = { hashtagsInput = it },
                    label = { Text("Hashtags (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val tags = hashtagsInput.split(",").map { it.trim() }.filter { it.isNotBlank() }
                    onSave(destination, title, hook, caption, tags)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
            ) {
                Text("Generate Derivative", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Slate400) }
        },
        containerColor = Slate900
    )
}
