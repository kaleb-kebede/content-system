package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.AdditionalModulesDao
import com.example.creatorgrowthos.data.local.entity.AppSettingsEntity
import com.example.creatorgrowthos.data.local.entity.BrandAssetEntity
import com.example.creatorgrowthos.data.local.entity.ContentTemplateEntity
import com.example.creatorgrowthos.data.local.entity.MonetizationRecordEntity
import com.example.creatorgrowthos.data.local.entity.ProductIdeaEntity
import com.example.creatorgrowthos.data.local.entity.ResearchResourceEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dao: AdditionalModulesDao
) {
    fun observeSettings(): Flow<AppSettingsEntity?> = dao.observeSettings()

    suspend fun getSettings(): AppSettingsEntity = dao.getSettings() ?: AppSettingsEntity()

    suspend fun saveSettings(settings: AppSettingsEntity) {
        dao.saveSettings(settings)
    }

    // Product Ideas
    fun getAllProductIdeas(): Flow<List<ProductIdeaEntity>> = dao.getAllProductIdeas()
    suspend fun saveProductIdea(idea: ProductIdeaEntity) = dao.insertProductIdea(idea)
    suspend fun deleteProductIdea(id: String) = dao.deleteProductIdea(id)

    // Monetization
    fun getAllMonetizationRecords(): Flow<List<MonetizationRecordEntity>> = dao.getAllMonetizationRecords()
    fun observeTotalEarnings(): Flow<Double?> = dao.observeTotalEarnings()
    suspend fun saveMonetizationRecord(record: MonetizationRecordEntity) = dao.insertMonetizationRecord(record)
    suspend fun deleteMonetizationRecord(id: String) = dao.deleteMonetizationRecord(id)

    // Brand Assets
    fun getAllBrandAssets(): Flow<List<BrandAssetEntity>> = dao.getAllBrandAssets()
    suspend fun saveBrandAsset(asset: BrandAssetEntity) = dao.insertBrandAsset(asset)
    suspend fun deleteBrandAsset(id: String) = dao.deleteBrandAsset(id)

    // Research
    fun getAllResearchResources(): Flow<List<ResearchResourceEntity>> = dao.getAllResearchResources()
    suspend fun saveResearchResource(resource: ResearchResourceEntity) = dao.insertResearchResource(resource)
    suspend fun deleteResearchResource(id: String) = dao.deleteResearchResource(id)

    // Templates
    fun getAllTemplates(): Flow<List<ContentTemplateEntity>> = dao.getAllTemplates()
    suspend fun saveTemplate(template: ContentTemplateEntity) = dao.insertTemplate(template)
}
