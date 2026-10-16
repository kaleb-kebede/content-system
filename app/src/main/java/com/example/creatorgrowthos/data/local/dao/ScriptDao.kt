package com.example.creatorgrowthos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.creatorgrowthos.data.local.entity.ScriptEntity
import com.example.creatorgrowthos.data.local.entity.ScriptSectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM scripts ORDER BY updatedAtUtc DESC")
    fun getAllScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM scripts WHERE id = :id")
    suspend fun getScriptById(id: String): ScriptEntity?

    @Query("SELECT * FROM scripts WHERE id = :id")
    fun observeScriptById(id: String): Flow<ScriptEntity?>

    @Query("SELECT * FROM scripts WHERE contentItemId = :contentItemId LIMIT 1")
    suspend fun getScriptForContentItem(contentItemId: String): ScriptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: ScriptEntity)

    @Update
    suspend fun updateScript(script: ScriptEntity)

    @Query("DELETE FROM scripts WHERE id = :id")
    suspend fun deleteScript(id: String)

    // Sections
    @Query("SELECT * FROM script_sections WHERE scriptId = :scriptId ORDER BY sectionOrder ASC")
    fun getSectionsForScript(scriptId: String): Flow<List<ScriptSectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<ScriptSectionEntity>)

    @Query("DELETE FROM script_sections WHERE scriptId = :scriptId")
    suspend fun deleteSectionsForScript(scriptId: String)

    @Transaction
    suspend fun replaceScriptWithSections(script: ScriptEntity, sections: List<ScriptSectionEntity>) {
        insertScript(script)
        deleteSectionsForScript(script.id)
        insertSections(sections)
    }
}
