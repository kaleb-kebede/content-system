package com.example.creatorgrowthos.ui.screens.publishing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ChecklistEntity
import com.example.creatorgrowthos.data.local.entity.ChecklistItemEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.data.repository.PublishingRepository
import com.example.creatorgrowthos.domain.publishing.PlatformLaunchResult
import com.example.creatorgrowthos.domain.publishing.PlatformPublisher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PublishingUiState(
    val publication: ContentPublicationEntity? = null,
    val checklist: ChecklistEntity? = null,
    val checklistItems: List<ChecklistItemEntity> = emptyList(),
    val launchResult: PlatformLaunchResult? = null,
    val captionCopied: Boolean = false,
    val showConfirmDialog: Boolean = false,
    val isConfirmed: Boolean = false
)

class PublishingViewModel(
    private val publishingRepository: PublishingRepository,
    private val platformPublisher: PlatformPublisher
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishingUiState())
    val uiState: StateFlow<PublishingUiState> = _uiState.asStateFlow()

    fun loadPublication(publicationId: String) {
        viewModelScope.launch {
            val pub = publishingRepository.getPublicationById(publicationId)
            if (pub != null) {
                _uiState.value = _uiState.value.copy(
                    publication = pub,
                    isConfirmed = pub.publicationStatus.isConfirmed()
                )

                // Load checklist
                publishingRepository.getChecklistForPublication(publicationId).collect { checklist ->
                    if (checklist != null) {
                        publishingRepository.getChecklistItems(checklist.id).collect { items ->
                            _uiState.value = _uiState.value.copy(
                                checklist = checklist,
                                checklistItems = items
                            )
                        }
                    }
                }
            }
        }
    }

    fun copyCaption() {
        val pub = _uiState.value.publication ?: return
        val fullCaption = buildString {
            append(pub.caption)
            if (pub.hashtags.isNotEmpty()) {
                append("\n\n")
                append(pub.hashtags.joinToString(" ") { if (it.startsWith("#")) it else "#$it" })
            }
        }
        val success = platformPublisher.copyToClipboard("Post Caption", fullCaption)
        _uiState.value = _uiState.value.copy(captionCopied = success)
    }

    fun launchPlatformApp() {
        val pub = _uiState.value.publication ?: return
        val result = platformPublisher.launchPlatform(pub.destination, pub.mediaReferenceUri)
        _uiState.value = _uiState.value.copy(launchResult = result)
    }

    fun toggleChecklistItem(itemId: String, isChecked: Boolean) {
        viewModelScope.launch {
            publishingRepository.setChecklistItemChecked(itemId, isChecked)
        }
    }

    fun openConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = true)
    }

    fun dismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = false)
    }

    fun confirmPublication(url: String) {
        val pub = _uiState.value.publication ?: return
        viewModelScope.launch {
            publishingRepository.confirmPublication(pub.id, url)
            _uiState.value = _uiState.value.copy(
                showConfirmDialog = false,
                isConfirmed = true
            )
            // Reload publication record
            loadPublication(pub.id)
        }
    }
}
