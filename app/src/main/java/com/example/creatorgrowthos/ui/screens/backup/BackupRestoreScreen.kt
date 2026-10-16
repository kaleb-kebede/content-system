package com.example.creatorgrowthos.ui.screens.backup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creatorgrowthos.theme.*

@Composable
fun BackupRestoreScreen(
    viewModel: BackupViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var restoreInput by remember { mutableStateOf("") }
    var showConfirmRestoreDialog by remember { mutableStateOf(false) }

    if (showConfirmRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmRestoreDialog = false },
            title = { Text("Confirm Database Restore", color = AmberWarningLight, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Restoring data will merge records into your local database. Do you wish to proceed?",
                    color = Slate200
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.restoreBackup(restoreInput, replaceExisting = false)
                        showConfirmRestoreDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary)
                ) {
                    Text("Proceed Restore", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmRestoreDialog = false }) {
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
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = "DATA MANAGEMENT & PORTABILITY",
                    style = Typography.labelSmall,
                    color = ElectricBluePrimary,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Backup & Export",
                    style = Typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate100
                )
            }
        }

        uiState.statusMessage?.let { status ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(12.dp),
                        style = Typography.bodyMedium,
                        color = ElectricBluePrimary
                    )
                }
            }
        }

        // 1. JSON Backup Export
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = ElectricBluePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "1. Export Full Database (JSON)",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Exports ideas, pipeline items, scripts, tasks, and historical analytics into an offline portable JSON backup.",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.generateBackup() },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBluePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Generate JSON Backup", color = Slate950, fontWeight = FontWeight.Bold)
                    }

                    uiState.exportJsonText?.let { json ->
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = json.take(300) + "... [${json.length} characters]",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // 2. CSV Analytics Export
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TableChart, contentDescription = null, tint = MintProgressLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2. Export Analytics to CSV",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Generates a structured spreadsheet CSV file with all historical views, CTR, watch time, and engagement rates.",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.generateCsvExport() },
                        colors = ButtonDefaults.buttonColors(containerColor = MintProgress),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Export CSV File", color = Slate950, fontWeight = FontWeight.Bold)
                    }

                    uiState.exportCsvText?.let { csv ->
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = csv.lines().take(5).joinToString("\n") + "\n...",
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }
        }

        // 3. Restore Backup
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate850),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AmberWarningLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "3. Restore Local Database",
                            style = Typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Slate100
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Paste your JSON backup text below. The system validates schema integrity before committing changes.",
                        style = Typography.bodyMedium,
                        color = Slate400
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = restoreInput,
                        onValueChange = {
                            restoreInput = it
                            if (it.isNotBlank()) viewModel.validateBackupContent(it)
                        },
                        placeholder = { Text("Paste JSON backup payload here...", color = Slate600) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showConfirmRestoreDialog = true },
                        enabled = uiState.validationResult?.isValid == true,
                        colors = ButtonDefaults.buttonColors(containerColor = AmberWarningLight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Validate & Restore", color = Slate950, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
