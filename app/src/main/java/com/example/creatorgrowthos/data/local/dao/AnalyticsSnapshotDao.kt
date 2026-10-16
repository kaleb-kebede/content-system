package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.AnalyticsSnapshotEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsSnapshotDao {
    @Query("SELECT * FROM analytics_snapshots WHERE publicationId = :publicationId ORDER BY snapshotDateUtc DESC")
    fun getSnapshotsForPublication(publicationId: String): Flow<List<AnalyticsSnapshotEntity>>

    @Query("SELECT * FROM analytics_snapshots WHERE publicationId = :publicationId ORDER BY snapshotDateUtc DESC")
    suspend fun getSnapshotsForPublicationList(publicationId: String): List<AnalyticsSnapshotEntity>

    @Query("SELECT * FROM analytics_snapshots WHERE platform = :platform ORDER BY snapshotDateUtc DESC")
    fun getSnapshotsForPlatform(platform: PlatformType): Flow<List<AnalyticsSnapshotEntity>>

    @Query("SELECT * FROM analytics_snapshots WHERE platform = :platform ORDER BY snapshotDateUtc DESC")
    suspend fun getSnapshotsForPlatformList(platform: PlatformType): List<AnalyticsSnapshotEntity>

    @Query("SELECT * FROM analytics_snapshots ORDER BY snapshotDateUtc DESC LIMIT 100")
    fun getAllRecentSnapshots(): Flow<List<AnalyticsSnapshotEntity>>

    @Query("SELECT * FROM analytics_snapshots ORDER BY snapshotDateUtc DESC")
    suspend fun getAllSnapshotsList(): List<AnalyticsSnapshotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: AnalyticsSnapshotEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshots(snapshots: List<AnalyticsSnapshotEntity>)

    @Update
    suspend fun updateSnapshot(snapshot: AnalyticsSnapshotEntity)

    @Query("DELETE FROM analytics_snapshots WHERE id = :id")
    suspend fun deleteSnapshot(id: String)
}
