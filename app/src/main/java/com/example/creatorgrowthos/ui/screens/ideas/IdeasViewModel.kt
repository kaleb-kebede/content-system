package com.example.creatorgrowthos.ui.screens.ideas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import com.example.creatorgrowthos.data.repository.IdeasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class IdeasUiState(
    val ideas: List<ContentIdeaEntity> = emptyList(),
    val searchQuery: String = "",
    val minDemandFilter: Int = 1,
    val showCreateDialog: Boolean = false,
    val newlyPromotedContentId: String? = null
)

class IdeasViewModel(
    private val ideasRepository: IdeasRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _minDemandFilter = MutableStateFlow(1)
    private val _showCreateDialog = MutableStateFlow(false)
    private val _promotedId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<IdeasUiState> = combine(
        ideasRepository.getAllIdeas(),
        _searchQuery,
        _minDemandFilter,
        _showCreateDialog,
        _promotedId
    ) { ideas, query, minDemand, showDialog, promotedId ->
        val filtered = ideas.filter {
            (query.isBlank() || it.title.contains(query, ignoreCase = true) || it.rawIdea.contains(query, ignoreCase = true)) &&
                    it.demandScore >= minDemand
        }
        IdeasUiState(
            ideas = filtered,
            searchQuery = query,
            minDemandFilter = minDemand,
            showCreateDialog = showDialog,
            newlyPromotedContentId = promotedId
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        IdeasUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMinDemandFilter(minScore: Int) {
        _minDemandFilter.value = minScore
    }

    fun openCreateDialog() {
        _showCreateDialog.value = true
    }

    fun dismissCreateDialog() {
        _showCreateDialog.value = false
    }

    fun quickCapture(title: String, rawIdea: String = "") {
        viewModelScope.launch {
            ideasRepository.quickCapture(title, rawIdea)
            dismissCreateDialog()
        }
    }

    fun saveIdea(idea: ContentIdeaEntity) {
        viewModelScope.launch {
            ideasRepository.saveIdea(idea)
            dismissCreateDialog()
        }
    }

    fun promoteIdeaToPipeline(ideaId: String) {
        viewModelScope.launch {
            val item = ideasRepository.convertIdeaToContentItem(ideaId)
            _promotedId.value = item?.id
        }
    }

    fun clearPromotedId() {
        _promotedId.value = null
    }

    fun deleteIdea(ideaId: String) {
        viewModelScope.launch {
            ideasRepository.deleteIdea(ideaId)
        }
    }
}
