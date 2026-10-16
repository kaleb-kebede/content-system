package com.example.creatorgrowthos.ui.screens.repurposing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentDerivativeEntity
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.repository.ContentRepository
import com.example.creatorgrowthos.data.repository.RepurposingRepository
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RepurposingUiState(
    val masterItems: List<ContentItemEntity> = emptyList(),
    val allDerivatives: List<ContentDerivativeEntity> = emptyList(),
    val selectedMasterId: String? = null,
    val showCreateDialog: Boolean = false
)

class RepurposingViewModel(
    private val repurposingRepository: RepurposingRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _selectedMasterId = MutableStateFlow<String?>(null)
    private val _showCreateDialog = MutableStateFlow(false)

    val uiState: StateFlow<RepurposingUiState> = combine(
        contentRepository.getAllContentItems(),
        repurposingRepository.getAllDerivatives(),
        _selectedMasterId,
        _showCreateDialog
    ) { items, derivatives, selectedMaster, showDialog ->
        RepurposingUiState(
            masterItems = items.filter { it.isMaster },
            allDerivatives = derivatives,
            selectedMasterId = selectedMaster,
            showCreateDialog = showDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        RepurposingUiState()
    )

    fun selectMasterItem(masterId: String?) {
        _selectedMasterId.value = masterId
    }

    fun openCreateDialog() {
        _showCreateDialog.value = true
    }

    fun dismissCreateDialog() {
        _showCreateDialog.value = false
    }

    fun createDerivative(
        masterId: String,
        destination: PlatformType,
        title: String,
        hook: String,
        caption: String,
        hashtags: List<String>,
        editingNotes: String = "",
        plannedPublicationUtc: Long? = null
    ) {
        viewModelScope.launch {
            repurposingRepository.createDerivative(
                masterContentId = masterId,
                destination = destination,
                title = title,
                hook = hook,
                caption = caption,
                hashtags = hashtags,
                editingNotes = editingNotes,
                plannedPublicationUtc = plannedPublicationUtc
            )
            dismissCreateDialog()
        }
    }

    fun deleteDerivative(id: String) {
        viewModelScope.launch {
            repurposingRepository.deleteDerivative(id)
        }
    }
}
