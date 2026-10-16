package com.example.creatorgrowthos.ui.screens.publishing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.theme.*

@Composable
fun AssistedPublishingScreen(
    publicationId: String,
    viewModel: PublishingViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var publishedUrlInput by remember { mutableStateOf("") }

    LaunchedEffect(publicationId) {
        viewModel.loadPublication(publicationId)
    }

    if (uiState.showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissConfirmDialog() },
            title = { Text("Confirm Successful Publication", color = Slate100, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Enter the live URL of your published post to save the official record and schedule your 48h analytics follow-up:",
                        color = Slate300
                    )
                    OutlinedTextField(
                        value = publishedUrlInput,
                        onValueChange = { publishedUrlInput = it },
                        placeholder = { Text("https://youtube.com/shorts/... or https://tiktok.com/...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmPublication(publishedUrlInput.ifBlank { "https://${uiState.publication?.destination?.packageName}/post" })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintProgress)
                ) {
                    Text("Confirm & Record", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDialog() }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Slate900)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "ASSISTED PUBLISHING WORKFLOW",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = uiState.publication?.platformSpecificTitle?.ifBlank { "Publish Post" } ?: "Publish Post",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Text(
                    text = "Destination: ${uiState.publication?.destination?.displayName ?: ""}",
                    style = Typography.titleMedium,
                    color = AmberWarningLight
                )
            }
        }

        // Post Status Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isConfirmed) MintProgress.copy(alpha = 0.15f) else Slate850
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (uiState.isConfirmed) MintProgressLight else Slate700)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (uiState.isConfirmed) Icons.Default.CheckCircle else Icons.Default.Link,
                        contentDescription = null,
                        tint = if (uiState.isConfirmed) MintProgressLight else ElectricBluePrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (uiState.isConfirmed) "PUBLICATION CONFIRMED" else "STATUS: ${uiState.publication?.publicationStatus?.displayName}",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isConfirmed) MintProgressLight else Slate100
                        )
                        if (uiState.publication?.publishedUrl != null) {
                            Text(
                                text = "URL: ${uiState.publication?.publishedUrl}",
                                style = Typography.bodyMedium,
                                color = Slate400
                            )
                        }
                    }
                }
            }
        }

        // Step 1: Caption & Metadata Copy
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Final Caption & Hashtags",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.publication?.caption ?: "No caption prepared.",
                        style = Typography.bodyLarge,
                        color = Slate200
                    )

                    if (uiState.publication?.hashtags?.isNotEmpty() == true) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = uiState.publication!!.hashtags.joinToString(" ") { if (it.startsWith("#")) it else "#$it" },
                            style = Typography.bodyMedium,
                            color = ElectricBluePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.copyCaption() },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (uiState.captionCopied) MintProgressLight else ElectricBluePrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (uiState.captionCopied) "Caption Copied!" else "Copy Full Caption")
                    }
                }
            }
        }

        // Step 2: Open Official Platform Application
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Open Official Platform App",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Launches verified Android app intent for ${uiState.publication?.destination?.displayName}. (Browser fallback if app not installed).",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.launchPlatformApp() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Slate950)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Open ${uiState.publication?.destination?.displayName ?: "Platform"}",
                            color = Slate950,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    uiState.launchResult?.let { res ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = res.message,
                            style = Typography.labelSmall,
                            color = if (res.success) MintProgressLight else CrimsonCriticalLight
                        )
                    }
                }
            }
        }

        // Step 3: Publication Checklist
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Publication Verification Checklist",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    uiState.checklistItems.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { viewModel.toggleChecklistItem(item.id, it) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MintProgress,
                                    uncheckedColor = Slate700
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = item.text,
                                style = Typography.bodyMedium,
                                color = if (item.isChecked) Slate400 else Slate100
                            )
                        }
                    }
                }
            }
        }

        // Step 4: Confirm Publication Button
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Final Step: Confirm Publication",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Once you complete publishing inside the platform, confirm it here to officially mark it published and update your weekly goal progress.",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.openConfirmDialog() },
                        enabled = !uiState.isConfirmed,
                        colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Slate950)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isConfirmed) "Already Confirmed" else "Confirm Publication",
                            color = Slate950,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
