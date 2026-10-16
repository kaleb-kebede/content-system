package com.example.creatorgrowthos.ui.screens.pipeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentItemEntity
import com.example.creatorgrowthos.data.repository.ContentRepository
import com.example.creatorgrowthos.data.repository.StageTransitionResult
import com.example.creatorgrowthos.domain.model.WorkflowStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PipelineUiState(
    val items: List<ContentItemEntity> = emptyList(),
    val isKanbanView: Boolean = true,
    val selectedStageFilter: WorkflowStage? = null,
    val selectedItem: ContentItemEntity? = null,
    val validationErrorMessage: String? = null
)

class PipelineViewModel(
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _isKanbanView = MutableStateFlow(true)
    private val _selectedStageFilter = MutableStateFlow<WorkflowStage?>(null)
    private val _validationErrorMessage = MutableStateFlow<String?>(null)
    val validationErrorMessage = _validationErrorMessage.asStateFlow()

    val uiState: StateFlow<PipelineUiState> = combine(
        contentRepository.getAllContentItems(),
        _isKanbanView,
        _selectedStageFilter,
        _validationErrorMessage
    ) { items, isKanban, filter, errorMsg ->
        val filtered = if (filter != null) items.filter { it.status == filter } else items
        PipelineUiState(
            items = filtered,
            isKanbanView = isKanban,
            selectedStageFilter = filter,
            validationErrorMessage = errorMsg
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        PipelineUiState()
    )

    fun toggleViewMode() {
        _isKanbanView.value = !_isKanbanView.value
    }

    fun setStageFilter(stage: WorkflowStage?) {
        _selectedStageFilter.value = stage
    }

    fun clearError() {
        _validationErrorMessage.value = null
    }

    fun transitionItemStage(itemId: String, targetStage: WorkflowStage, reason: String = "") {
        viewModelScope.launch {
            when (val result = contentRepository.transitionStage(itemId, targetStage, reason)) {
                is StageTransitionResult.Success -> {
                    _validationErrorMessage.value = null
                }
                is StageTransitionResult.ValidationFailed -> {
                    _validationErrorMessage.value = result.reason
                }
                is StageTransitionResult.ItemNotFound -> {
                    _validationErrorMessage.value = "Content item not found."
                }
            }
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            contentRepository.deleteContentItem(itemId)
        }
    }
}
