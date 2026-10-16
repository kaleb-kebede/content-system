package com.example.creatorgrowthos.ui.screens.extra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.creatorgrowthos.data.local.entity.BrandAssetEntity
import com.example.creatorgrowthos.data.local.entity.ContentPillarEntity
import com.example.creatorgrowthos.data.local.entity.ContentTemplateEntity
import com.example.creatorgrowthos.data.local.entity.MonetizationRecordEntity
import com.example.creatorgrowthos.data.local.entity.ProductIdeaEntity
import com.example.creatorgrowthos.data.local.entity.ResearchResourceEntity
import com.example.creatorgrowthos.data.local.entity.ReviewEntity
import com.example.creatorgrowthos.data.repository.SettingsRepository
import com.example.creatorgrowthos.data.repository.StrategyGoalsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExtrasUiState(
    val productIdeas: List<ProductIdeaEntity> = emptyList(),
    val monetizationRecords: List<MonetizationRecordEntity> = emptyList(),
    val totalEarnings: Double = 0.0,
    val brandAssets: List<BrandAssetEntity> = emptyList(),
    val researchResources: List<ResearchResourceEntity> = emptyList(),
    val templates: List<ContentTemplateEntity> = emptyList(),
    val pillars: List<ContentPillarEntity> = emptyList(),
    val reviews: List<ReviewEntity> = emptyList()
)

class ExtrasViewModel(
    private val settingsRepository: SettingsRepository,
    private val strategyGoalsRepository: StrategyGoalsRepository
) : ViewModel() {

    val uiState: StateFlow<ExtrasUiState> = combine(
        settingsRepository.getAllProductIdeas(),
        settingsRepository.getAllMonetizationRecords(),
        settingsRepository.observeTotalEarnings(),
        settingsRepository.getAllBrandAssets(),
        settingsRepository.getAllResearchResources(),
        settingsRepository.getAllTemplates(),
        strategyGoalsRepository.getActivePillars(),
        strategyGoalsRepository.getAllReviews()
    ) { args: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        ExtrasUiState(
            productIdeas = args[0] as List<ProductIdeaEntity>,
            monetizationRecords = args[1] as List<MonetizationRecordEntity>,
            totalEarnings = (args[2] as Double?) ?: 0.0,
            brandAssets = args[3] as List<BrandAssetEntity>,
            researchResources = args[4] as List<ResearchResourceEntity>,
            templates = args[5] as List<ContentTemplateEntity>,
            pillars = args[6] as List<ContentPillarEntity>,
            reviews = args[7] as List<ReviewEntity>
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ExtrasUiState()
    )

    fun addProductIdea(idea: ProductIdeaEntity) {
        viewModelScope.launch { settingsRepository.saveProductIdea(idea) }
    }

    fun deleteProductIdea(id: String) {
        viewModelScope.launch { settingsRepository.deleteProductIdea(id) }
    }

    fun addMonetizationRecord(record: MonetizationRecordEntity) {
        viewModelScope.launch { settingsRepository.saveMonetizationRecord(record) }
    }

    fun deleteMonetizationRecord(id: String) {
        viewModelScope.launch { settingsRepository.deleteMonetizationRecord(id) }
    }

    fun addBrandAsset(asset: BrandAssetEntity) {
        viewModelScope.launch { settingsRepository.saveBrandAsset(asset) }
    }

    fun deleteBrandAsset(id: String) {
        viewModelScope.launch { settingsRepository.deleteBrandAsset(id) }
    }

    fun addResearch(resource: ResearchResourceEntity) {
        viewModelScope.launch { settingsRepository.saveResearchResource(resource) }
    }

    fun deleteResearch(id: String) {
        viewModelScope.launch { settingsRepository.deleteResearchResource(id) }
    }

    fun addTemplate(template: ContentTemplateEntity) {
        viewModelScope.launch { settingsRepository.saveTemplate(template) }
    }

    fun addPillar(pillar: ContentPillarEntity) {
        viewModelScope.launch { strategyGoalsRepository.savePillar(pillar) }
    }

    fun deletePillar(id: String) {
        viewModelScope.launch { strategyGoalsRepository.deletePillar(id) }
    }

    fun addReview(review: ReviewEntity) {
        viewModelScope.launch { strategyGoalsRepository.saveReview(review) }
    }
}
