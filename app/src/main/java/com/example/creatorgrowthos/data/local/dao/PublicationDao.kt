package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicationDao {
    @Query("SELECT * FROM content_publications ORDER BY plannedPublicationUtc ASC")
    fun getAllPublications(): Flow<List<ContentPublicationEntity>>

    @Query("SELECT * FROM content_publications WHERE id = :id")
    suspend fun getPublicationById(id: String): ContentPublicationEntity?

    @Query("SELECT * FROM content_publications WHERE id = :id")
    fun observePublicationById(id: String): Flow<ContentPublicationEntity?>

    @Query("SELECT * FROM content_publications WHERE contentItemId = :contentItemId")
    fun getPublicationsForContent(contentItemId: String): Flow<List<ContentPublicationEntity>>

    @Query("SELECT * FROM content_publications WHERE plannedPublicationUtc BETWEEN :startOfDayUtc AND :endOfDayUtc ORDER BY plannedPublicationUtc ASC")
    fun getPublicationsScheduledForDay(startOfDayUtc: Long, endOfDayUtc: Long): Flow<List<ContentPublicationEntity>>

    @Query("SELECT * FROM content_publications WHERE publicationStatus = 'PUBLISHING_IN_PROGRESS' OR (publicationStatus = 'REMINDER_SENT' AND actualPublicationUtc IS NULL)")
    fun getPublicationsAwaitingConfirmation(): Flow<List<ContentPublicationEntity>>

    @Query("SELECT * FROM content_publications WHERE destination = :destination AND publicationStatus = 'PUBLISHED_CONFIRMED' AND actualPublicationUtc BETWEEN :startUtc AND :endUtc")
    suspend fun getConfirmedPublicationsForDestination(destination: PlatformType, startUtc: Long, endUtc: Long): List<ContentPublicationEntity>

    @Query("SELECT * FROM content_publications WHERE publicationStatus = 'PUBLISHED_CONFIRMED' AND actualPublicationUtc BETWEEN :startUtc AND :endUtc")
    fun observeConfirmedPublicationsBetween(startUtc: Long, endUtc: Long): Flow<List<ContentPublicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublication(publication: ContentPublicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPublications(publications: List<ContentPublicationEntity>)

    @Update
    suspend fun updatePublication(publication: ContentPublicationEntity)

    @Query("UPDATE content_publications SET publicationStatus = :status, actualPublicationUtc = :actualTimeUtc, publishedUrl = :url WHERE id = :id")
    suspend fun confirmPublication(id: String, actualTimeUtc: Long, url: String, status: PublicationStatus = PublicationStatus.PUBLISHED_CONFIRMED)

    @Query("DELETE FROM content_publications WHERE id = :id")
    suspend fun deletePublication(id: String)
}
