package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ContentIdeaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentIdeaDao {
    @Query("SELECT * FROM content_ideas ORDER BY captureDateUtc DESC")
    fun getAllIdeas(): Flow<List<ContentIdeaEntity>>

    @Query("SELECT * FROM content_ideas WHERE id = :id")
    suspend fun getIdeaById(id: String): ContentIdeaEntity?

    @Query("SELECT * FROM content_ideas WHERE status = :status ORDER BY demandScore DESC, captureDateUtc DESC")
    fun getIdeasByStatus(status: String): Flow<List<ContentIdeaEntity>>

    @Query("SELECT * FROM content_ideas WHERE demandScore >= :minScore ORDER BY demandScore DESC")
    fun getHighDemandIdeas(minScore: Int = 7): Flow<List<ContentIdeaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdea(idea: ContentIdeaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdeas(ideas: List<ContentIdeaEntity>)

    @Update
    suspend fun updateIdea(idea: ContentIdeaEntity)

    @Query("UPDATE content_ideas SET status = :status, convertedContentItemId = :contentItemId WHERE id = :ideaId")
    suspend fun markIdeaPromoted(ideaId: String, contentItemId: String, status: String = "PROMOTED")

    @Query("DELETE FROM content_ideas WHERE id = :id")
    suspend fun deleteIdea(id: String)

    @Query("SELECT COUNT(*) FROM content_ideas")
    suspend fun countIdeas(): Int
}
