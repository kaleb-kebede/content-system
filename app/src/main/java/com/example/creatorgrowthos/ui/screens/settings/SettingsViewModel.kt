package com.example.creatorgrowthos.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.AppSettingsEntity
import com.example.creatorgrowthos.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: AppSettingsEntity = AppSettingsEntity(),
    val savedNotification: Boolean = false
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = MutableStateFlow(SettingsUiState()).apply {
        viewModelScope.launch {
            settingsRepository.observeSettings().collect { s ->
                if (s != null) {
                    value = value.copy(settings = s)
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SettingsUiState()
    )

    fun updateSettings(updated: AppSettingsEntity) {
        viewModelScope.launch {
            settingsRepository.saveSettings(updated)
        }
    }
}
