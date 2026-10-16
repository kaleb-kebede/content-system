package com.example.creatorgrowthos.ui.screens.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.repository.BackupRestoreRepository
import com.example.creatorgrowthos.data.repository.RestoreValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BackupUiState(
    val exportJsonText: String? = null,
    val exportCsvText: String? = null,
    val validationResult: RestoreValidationResult? = null,
    val restoreSuccess: Boolean? = null,
    val statusMessage: String? = null,
    val isBusy: Boolean = false
)

class BackupViewModel(
    private val backupRepository: BackupRestoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BackupUiState())
    val uiState: StateFlow<BackupUiState> = _uiState.asStateFlow()

    fun generateBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, statusMessage = "Generating JSON backup...")
            try {
                val json = backupRepository.createJsonBackup()
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    exportJsonText = json,
                    statusMessage = "Backup generated successfully (${json.length} characters)"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    statusMessage = "Backup failed: ${e.localizedMessage}"
                )
            }
        }
    }

    fun generateCsvExport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, statusMessage = "Generating CSV analytics...")
            try {
                val csv = backupRepository.exportAnalyticsToCsv()
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    exportCsvText = csv,
                    statusMessage = "CSV export ready"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    statusMessage = "CSV export failed: ${e.localizedMessage}"
                )
            }
        }
    }

    fun validateBackupContent(content: String) {
        val result = backupRepository.validateBackup(content)
        _uiState.value = _uiState.value.copy(
            validationResult = result,
            statusMessage = if (result.isValid) "Valid backup: ${result.itemCountSummary}" else "Invalid: ${result.errorMessage}"
        )
    }

    fun restoreBackup(content: String, replaceExisting: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isBusy = true, statusMessage = "Restoring database...")
            val success = backupRepository.restoreFromJson(content, replaceExisting)
            _uiState.value = _uiState.value.copy(
                isBusy = false,
                restoreSuccess = success,
                statusMessage = if (success) "Restore completed successfully!" else "Restore failed."
            )
        }
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(statusMessage = null, validationResult = null)
    }
}
