package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentDerivativeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDerivativeDao {
    @Query("SELECT * FROM content_derivatives WHERE masterContentId = :masterId ORDER BY plannedPublicationUtc ASC")
    fun getDerivativesForMaster(masterId: String): Flow<List<ContentDerivativeEntity>>

    @Query("SELECT * FROM content_derivatives WHERE id = :id")
    suspend fun getDerivativeById(id: String): ContentDerivativeEntity?

    @Query("SELECT * FROM content_derivatives ORDER BY createdAtUtc DESC")
    fun getAllDerivatives(): Flow<List<ContentDerivativeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDerivative(derivative: ContentDerivativeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDerivatives(derivatives: List<ContentDerivativeEntity>)

    @Update
    suspend fun updateDerivative(derivative: ContentDerivativeEntity)

    @Query("DELETE FROM content_derivatives WHERE id = :id")
    suspend fun deleteDerivative(id: String)
}
