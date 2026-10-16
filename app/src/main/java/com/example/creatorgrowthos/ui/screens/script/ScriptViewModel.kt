package com.example.creatorgrowthos.ui.screens.script

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ScriptEntity
import com.example.creatorgrowthos.data.local.entity.ScriptSectionEntity
import com.example.creatorgrowthos.data.repository.ScriptRepository
import com.example.creatorgrowthos.domain.model.ContentFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ScriptUiState(
    val currentScript: ScriptEntity? = null,
    val sections: List<ScriptSectionEntity> = emptyList(),
    val estimatedDurationFormatted: String = "0:00",
    val wordCount: Int = 0,
    val isSaved: Boolean = true,
    val saveErrorMessage: String? = null
)

class ScriptViewModel(
    private val scriptRepository: ScriptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScriptUiState())
    val uiState: StateFlow<ScriptUiState> = _uiState.asStateFlow()

    fun loadScript(scriptId: String, contentItemId: String? = null, defaultFormat: ContentFormat = ContentFormat.SHORT_FORM_VIDEO) {
        viewModelScope.launch {
            if (scriptId == "new" || scriptId.isBlank()) {
                val newScript = scriptRepository.createNewScript(
                    title = "New Script ${System.currentTimeMillis() % 1000}",
                    format = defaultFormat,
                    contentItemId = contentItemId
                )
                observeScript(newScript.id)
            } else {
                observeScript(scriptId)
            }
        }
    }

    private fun observeScript(scriptId: String) {
        viewModelScope.launch {
            val script = scriptRepository.getScriptById(scriptId)
            if (script != null) {
                scriptRepository.getSectionsForScript(scriptId).collect { sections ->
                    val totalSec = script.estimatedDurationSeconds
                    val min = totalSec / 60
                    val sec = totalSec % 60
                    _uiState.value = _uiState.value.copy(
                        currentScript = script,
                        sections = sections,
                        wordCount = script.wordCount,
                        estimatedDurationFormatted = String.format("%d:%02d", min, sec)
                    )
                }
            }
        }
    }

    fun updateScriptBody(newBody: String) {
        val current = _uiState.value.currentScript ?: return
        val wordCount = newBody.split("\\s+".toRegex()).count { it.isNotBlank() }
        val durationSeconds = scriptRepository.calculateDurationSeconds(wordCount, current.narrationWordsPerMinute)
        val min = durationSeconds / 60
        val sec = durationSeconds % 60

        val updated = current.copy(
            scriptBody = newBody,
            wordCount = wordCount,
            estimatedDurationSeconds = durationSeconds
        )

        _uiState.value = _uiState.value.copy(
            currentScript = updated,
            wordCount = wordCount,
            estimatedDurationFormatted = String.format("%d:%02d", min, sec),
            isSaved = false
        )

        // Autosave
        viewModelScope.launch {
            scriptRepository.saveScript(updated)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }

    fun updateSection(sectionId: String, newWords: String, visualNotes: String = "") {
        val currentSections = _uiState.value.sections.toMutableList()
        val index = currentSections.indexOfFirst { it.id == sectionId }
        if (index != -1) {
            val updatedSection = currentSections[index].copy(
                spokenWords = newWords,
                visualNotes = if (visualNotes.isNotBlank()) visualNotes else currentSections[index].visualNotes
            )
            currentSections[index] = updatedSection

            val currentScript = _uiState.value.currentScript ?: return
            val combinedBody = currentSections.joinToString("\n\n") { "## ${it.title}\n${it.spokenWords}" }
            val wordCount = combinedBody.split("\\s+".toRegex()).count { it.isNotBlank() }
            val durationSeconds = scriptRepository.calculateDurationSeconds(wordCount, currentScript.narrationWordsPerMinute)

            val updatedScript = currentScript.copy(
                scriptBody = combinedBody,
                wordCount = wordCount,
                estimatedDurationSeconds = durationSeconds
            )

            val min = durationSeconds / 60
            val sec = durationSeconds % 60

            _uiState.value = _uiState.value.copy(
                currentScript = updatedScript,
                sections = currentSections,
                wordCount = wordCount,
                estimatedDurationFormatted = String.format("%d:%02d", min, sec),
                isSaved = false
            )

            viewModelScope.launch {
                scriptRepository.saveScript(updatedScript, currentSections)
                _uiState.value = _uiState.value.copy(isSaved = true)
            }
        }
    }
}
