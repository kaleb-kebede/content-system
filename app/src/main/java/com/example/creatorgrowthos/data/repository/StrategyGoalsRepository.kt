package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.ContentPillarDao
import com.example.creatorgrowthos.data.local.dao.GoalDao
import com.example.creatorgrowthos.data.local.dao.PlatformAccountDao
import com.example.creatorgrowthos.data.local.dao.ReviewDao
import com.example.creatorgrowthos.data.local.entity.ContentPillarEntity
import com.example.creatorgrowthos.data.local.entity.GoalEntity
import com.example.creatorgrowthos.data.local.entity.PlatformAccountEntity
import com.example.creatorgrowthos.data.local.entity.ReviewEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

data class PlatformGrowthProgress(
    val account: PlatformAccountEntity,
    val remainingGrowth: Long,
    val progressPercentage: Float
)

class StrategyGoalsRepository(
    private val platformAccountDao: PlatformAccountDao,
    private val pillarDao: ContentPillarDao,
    private val goalDao: GoalDao,
    private val reviewDao: ReviewDao
) {
    fun getAllAccounts(): Flow<List<PlatformAccountEntity>> = platformAccountDao.getAllAccounts()

    suspend fun updateAudienceCount(platform: PlatformType, newCount: Long) {
        platformAccountDao.updateAudienceCount(platform, newCount)
    }

    suspend fun updateWeeklyTarget(platform: PlatformType, target: Int) {
        platformAccountDao.updateWeeklyTarget(platform, target)
    }

    suspend fun getGrowthProgressForPlatform(platform: PlatformType): PlatformGrowthProgress? {
        val account = platformAccountDao.getAccountForPlatform(platform) ?: return null
        val target = account.targetAudienceCount
        val current = account.currentAudienceCount
        val remaining = (target - current).coerceAtLeast(0)
        val pct = if (target > 0) (current.toFloat() / target * 100f).coerceIn(0f, 100f) else 100f
        return PlatformGrowthProgress(account, remaining, pct)
    }

    // Pillars
    fun getActivePillars(): Flow<List<ContentPillarEntity>> = pillarDao.getActivePillars()

    suspend fun savePillar(pillar: ContentPillarEntity) {
        pillarDao.insertPillar(pillar)
    }

    suspend fun deletePillar(id: String) {
        pillarDao.deletePillar(id)
    }

    // Goals
    fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun saveGoal(goal: GoalEntity) {
        goalDao.insertGoal(goal)
    }

    suspend fun toggleGoalAchieved(id: String, isAchieved: Boolean) {
        goalDao.markGoalAchieved(id, isAchieved)
    }

    suspend fun deleteGoal(id: String) {
        goalDao.deleteGoal(id)
    }

    // Reviews
    fun getAllReviews(): Flow<List<ReviewEntity>> = reviewDao.getAllReviews()

    suspend fun saveReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
    }
}
