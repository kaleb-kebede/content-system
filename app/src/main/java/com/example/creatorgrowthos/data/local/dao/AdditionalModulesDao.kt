package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.AppSettingsEntity
import com.example.creatorgrowthos.data.local.entity.BrandAssetEntity
import com.example.creatorgrowthos.data.local.entity.ContentTemplateEntity
import com.example.creatorgrowthos.data.local.entity.MonetizationRecordEntity
import com.example.creatorgrowthos.data.local.entity.ProductIdeaEntity
import com.example.creatorgrowthos.data.local.entity.ResearchResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdditionalModulesDao {
    // App Settings
    @Query("SELECT * FROM app_settings WHERE id = 'user_settings' LIMIT 1")
    fun observeSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE id = 'user_settings' LIMIT 1")
    suspend fun getSettings(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)

    // Product Ideas
    @Query("SELECT * FROM product_ideas ORDER BY createdAtUtc DESC")
    fun getAllProductIdeas(): Flow<List<ProductIdeaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductIdea(idea: ProductIdeaEntity)

    @Update
    suspend fun updateProductIdea(idea: ProductIdeaEntity)

    @Query("DELETE FROM product_ideas WHERE id = :id")
    suspend fun deleteProductIdea(id: String)

    // Monetization
    @Query("SELECT * FROM monetization_records ORDER BY dateUtc DESC")
    fun getAllMonetizationRecords(): Flow<List<MonetizationRecordEntity>>

    @Query("SELECT SUM(amount) FROM monetization_records")
    fun observeTotalEarnings(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMonetizationRecord(record: MonetizationRecordEntity)

    @Query("DELETE FROM monetization_records WHERE id = :id")
    suspend fun deleteMonetizationRecord(id: String)

    // Brand Assets
    @Query("SELECT * FROM brand_assets")
    fun getAllBrandAssets(): Flow<List<BrandAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrandAsset(asset: BrandAssetEntity)

    @Query("DELETE FROM brand_assets WHERE id = :id")
    suspend fun deleteBrandAsset(id: String)

    // Research
    @Query("SELECT * FROM research_resources ORDER BY createdAtUtc DESC")
    fun getAllResearchResources(): Flow<List<ResearchResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResearchResource(resource: ResearchResourceEntity)

    @Query("DELETE FROM research_resources WHERE id = :id")
    suspend fun deleteResearchResource(id: String)

    // Templates
    @Query("SELECT * FROM content_templates")
    fun getAllTemplates(): Flow<List<ContentTemplateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: ContentTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<ContentTemplateEntity>)
}
