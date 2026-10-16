package com.example.creatorgrowthos.ui.screens.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.ContentPillarEntity
import com.example.creatorgrowthos.data.local.entity.GoalEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.data.repository.StrategyGoalsRepository
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GoalsUiState(
    val accounts: List<PlatformAccountEntity> = emptyList(),
    val pillars: List<ContentPillarEntity> = emptyList(),
    val goals: List<GoalEntity> = emptyList(),
    val showEditAudienceDialog: PlatformType? = null,
    val showAddGoalDialog: Boolean = false
)

class GoalsViewModel(
    private val strategyRepository: StrategyGoalsRepository
) : ViewModel() {

    private val _editAudiencePlatform = MutableStateFlow<PlatformType?>(null)
    private val _showAddGoalDialog = MutableStateFlow(false)

    val uiState: StateFlow<GoalsUiState> = combine(
        strategyRepository.getAllAccounts(),
        strategyRepository.getActivePillars(),
        strategyRepository.getAllGoals(),
        _editAudiencePlatform,
        _showAddGoalDialog
    ) { accounts, pillars, goals, editPlatform, showGoalDialog ->
        GoalsUiState(
            accounts = accounts,
            pillars = pillars,
            goals = goals,
            showEditAudienceDialog = editPlatform,
            showAddGoalDialog = showGoalDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        GoalsUiState()
    )

    fun openEditAudience(platform: PlatformType) {
        _editAudiencePlatform.value = platform
    }

    fun dismissEditAudience() {
        _editAudiencePlatform.value = null
    }

    fun updateAudience(platform: PlatformType, count: Long) {
        viewModelScope.launch {
            strategyRepository.updateAudienceCount(platform, count)
            dismissEditAudience()
        }
    }

    fun updateWeeklyTarget(platform: PlatformType, target: Int) {
        viewModelScope.launch {
            strategyRepository.updateWeeklyTarget(platform, target)
        }
    }

    fun openAddGoalDialog() {
        _showAddGoalDialog.value = true
    }

    fun dismissAddGoalDialog() {
        _showAddGoalDialog.value = false
    }

    fun saveGoal(goal: GoalEntity) {
        viewModelScope.launch {
            strategyRepository.saveGoal(goal)
            dismissAddGoalDialog()
        }
    }

    fun toggleGoal(goalId: String, achieved: Boolean) {
        viewModelScope.launch {
            strategyRepository.toggleGoalAchieved(goalId, achieved)
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            strategyRepository.deleteGoal(goalId)
        }
    }
}
