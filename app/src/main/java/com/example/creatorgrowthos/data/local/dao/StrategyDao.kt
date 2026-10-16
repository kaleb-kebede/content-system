package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentPillarEntity
import com.example.creatorgrowthos.data.local.entity.ContentTagEntity
import com.example.creatorgrowthos.data.local.entity.GoalEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.data.local.entity.ReviewEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.Flow

@Dao
interface PlatformAccountDao {
    @Query("SELECT * FROM platform_accounts")
    fun getAllAccounts(): Flow<List<PlatformAccountEntity>>

    @Query("SELECT * FROM platform_accounts")
    suspend fun getAllAccountsList(): List<PlatformAccountEntity>

    @Query("SELECT * FROM platform_accounts WHERE platform = :platform LIMIT 1")
    suspend fun getAccountForPlatform(platform: PlatformType): PlatformAccountEntity?

    @Query("SELECT * FROM platform_accounts WHERE platform = :platform LIMIT 1")
    fun observeAccountForPlatform(platform: PlatformType): Flow<PlatformAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: PlatformAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccounts(accounts: List<PlatformAccountEntity>)

    @Update
    suspend fun updateAccount(account: PlatformAccountEntity)

    @Query("UPDATE platform_accounts SET currentAudienceCount = :count, lastUpdatedUtc = :timestampUtc WHERE platform = :platform")
    suspend fun updateAudienceCount(platform: PlatformType, count: Long, timestampUtc: Long = System.currentTimeMillis())

    @Query("UPDATE platform_accounts SET weeklyPublishingTarget = :target WHERE platform = :platform")
    suspend fun updateWeeklyTarget(platform: PlatformType, target: Int)
}

@Dao
interface ContentPillarDao {
    @Query("SELECT * FROM content_pillars WHERE isActive = 1")
    fun getActivePillars(): Flow<List<ContentPillarEntity>>

    @Query("SELECT * FROM content_pillars")
    suspend fun getAllPillarsList(): List<ContentPillarEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPillar(pillar: ContentPillarEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPillars(pillars: List<ContentPillarEntity>)

    @Update
    suspend fun updatePillar(pillar: ContentPillarEntity)

    @Query("DELETE FROM content_pillars WHERE id = :id")
    suspend fun deletePillar(id: String)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY isAchieved ASC, deadlineUtc ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE isAchieved = 0")
    fun getActiveGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Query("UPDATE goals SET isAchieved = :achieved WHERE id = :id")
    suspend fun markGoalAchieved(id: String, achieved: Boolean)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteGoal(id: String)
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews ORDER BY timestampUtc DESC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Query("SELECT * FROM reviews WHERE periodString = :periodString LIMIT 1")
    suspend fun getReviewForPeriod(periodString: String): ReviewEntity?

    @Query("SELECT * FROM reviews WHERE reviewType = :type ORDER BY timestampUtc DESC LIMIT 10")
    fun getReviewsByType(type: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Update
    suspend fun updateReview(review: ReviewEntity)
}
