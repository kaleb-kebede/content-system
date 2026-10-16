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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
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
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter the live post URL to record the publication and initiate the 48h analytics tracking window:",
                        color = Slate300,
                        style = Typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = publishedUrlInput,
                        onValueChange = { publishedUrlInput = it },
                        placeholder = { Text("https://youtube.com/shorts/... or https://tiktok.com/@...", color = Slate500) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintProgressLight,
                            unfocusedBorderColor = BorderDefault,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100,
                            focusedContainerColor = Slate850,
                            unfocusedContainerColor = Slate850
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.confirmPublication(publishedUrlInput.ifBlank { "https://${uiState.publication?.destination?.packageName}/post" })
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Confirm & Record", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissConfirmDialog() }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(20.dp)
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
            Spacer(modifier = Modifier.height(10.dp))
            Column {
                Text(
                    text = "ASSISTED PUBLISHING WORKFLOW",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.4.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = uiState.publication?.platformSpecificTitle?.ifBlank { "Publish Post" } ?: "Publish Post",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Destination: ",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Slate800
                    ) {
                        Text(
                            text = uiState.publication?.destination?.displayName ?: "Platform",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = Typography.labelSmall,
                            color = AmberWarningLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Post Status Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isConfirmed) MintProgress.copy(alpha = 0.15f) else Slate850
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (uiState.isConfirmed) MintProgressLight.copy(alpha = 0.8f) else BorderDefault)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (uiState.isConfirmed) MintProgress.copy(alpha = 0.2f) else Slate800,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (uiState.isConfirmed) Icons.Default.CheckCircle else Icons.Default.Link,
                                contentDescription = null,
                                tint = if (uiState.isConfirmed) MintProgressLight else ElectricBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (uiState.isConfirmed) "PUBLICATION CONFIRMED" else "STATUS: ${uiState.publication?.publicationStatus?.displayName}",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isConfirmed) MintProgressLight else Slate100
                        )
                        if (uiState.publication?.publishedUrl != null) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Live URL: ${uiState.publication?.publishedUrl}",
                                style = Typography.bodySmall,
                                color = Slate300
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
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Final Caption & Hashtags",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                        if (uiState.captionCopied) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MintProgress.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Done, contentDescription = null, tint = MintProgressLight, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copied", style = Typography.labelSmall, color = MintProgressLight, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Slate900,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = uiState.publication?.caption ?: "No caption prepared.",
                                style = Typography.bodyLarge,
                                color = Slate200
                            )

                            if (uiState.publication?.hashtags?.isNotEmpty() == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.publication!!.hashtags.joinToString(" ") { if (it.startsWith("#")) it else "#$it" },
                                    style = Typography.bodyMedium,
                                    color = ElectricBluePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { viewModel.copyCaption() },
                        modifier = Modifier.height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (uiState.captionCopied) MintProgressLight else ElectricBluePrimary
                        ),
                        border = BorderStroke(1.dp, if (uiState.captionCopied) MintProgressLight else ElectricBluePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (uiState.captionCopied) "Caption Copied to Clipboard" else "Copy Full Caption")
                    }
                }
            }
        }

        // Step 2: Open Official Platform Application
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.launchPlatformApp() },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Slate950, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Launch ${uiState.publication?.destination?.displayName ?: "Platform"}",
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
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "3. Publication Verification Checklist",
                        style = Typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate100
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    uiState.checklistItems.forEach { item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (item.isChecked) Slate800.copy(alpha = 0.5f) else Slate800,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.isChecked,
                                    onCheckedChange = { viewModel.toggleChecklistItem(item.id, it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MintProgress,
                                        uncheckedColor = Slate600
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.text,
                                    style = Typography.bodyMedium,
                                    color = if (item.isChecked) Slate400 else Slate100,
                                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
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
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, BorderDefault)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.openConfirmDialog() },
                        enabled = !uiState.isConfirmed,
                        colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Slate950)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isConfirmed) "Already Confirmed" else "Confirm Publication",
                            color = Slate950,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

