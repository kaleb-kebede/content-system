package com.example.creatorgrowthos.ui.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.data.repository.AnalyticsRepository
import com.example.creatorgrowthos.data.repository.PlatformPerformanceOverview
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AnalyticsUiState(
    val selectedPlatform: PlatformType? = null, // null for Overview
    val recentSnapshots: List<AnalyticsSnapshotEntity> = emptyList(),
    val ytOverview: PlatformPerformanceOverview? = null,
    val tiktokOverview: PlatformPerformanceOverview? = null,
    val igOverview: PlatformPerformanceOverview? = null,
    val showAddDialog: Boolean = false
)

class AnalyticsViewModel(
    private val analyticsRepository: AnalyticsRepository
) : ViewModel() {

    private val _selectedPlatform = MutableStateFlow<PlatformType?>(null)
    private val _showAddDialog = MutableStateFlow(false)

    val uiState: StateFlow<AnalyticsUiState> = combine(
        analyticsRepository.getAllSnapshots(),
        _selectedPlatform,
        _showAddDialog
    ) { snapshots, platform, showDialog ->
        val filtered = if (platform != null) snapshots.filter { it.platform == platform } else snapshots
        val yt = analyticsRepository.getPerformanceOverviewForPlatform(PlatformType.YOUTUBE_SHORTS)
        val tt = analyticsRepository.getPerformanceOverviewForPlatform(PlatformType.TIKTOK)
        val ig = analyticsRepository.getPerformanceOverviewForPlatform(PlatformType.INSTAGRAM_REELS)

        AnalyticsUiState(
            selectedPlatform = platform,
            recentSnapshots = filtered,
            ytOverview = yt,
            tiktokOverview = tt,
            igOverview = ig,
            showAddDialog = showDialog
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AnalyticsUiState()
    )

    fun selectPlatform(platform: PlatformType?) {
        _selectedPlatform.value = platform
    }

    fun openAddSnapshotDialog() {
        _showAddDialog.value = true
    }

    fun dismissAddSnapshotDialog() {
        _showAddDialog.value = false
    }

    fun recordManualSnapshot(snapshot: AnalyticsSnapshotEntity) {
        viewModelScope.launch {
            analyticsRepository.recordManualSnapshot(snapshot)
            dismissAddSnapshotDialog()
        }
    }
}
