package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.ScriptDao
import com.example.creatorgrowthos.data.local.entity.ScriptEntity
import com.example.creatorgrowthos.data.local.entity.ScriptSectionEntity
import com.example.creatorgrowthos.domain.model.ContentFormat
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ScriptRepository(
    private val scriptDao: ScriptDao
) {
    fun getAllScripts(): Flow<List<ScriptEntity>> = scriptDao.getAllScripts()

    suspend fun getScriptById(id: String): ScriptEntity? = scriptDao.getScriptById(id)

    fun observeScriptById(id: String): Flow<ScriptEntity?> = scriptDao.observeScriptById(id)

    fun getSectionsForScript(scriptId: String): Flow<List<ScriptSectionEntity>> =
        scriptDao.getSectionsForScript(scriptId)

    suspend fun saveScript(script: ScriptEntity, sections: List<ScriptSectionEntity> = emptyList()) {
        val wordCount = calculateWordCount(script.scriptBody, sections)
        val durationSeconds = calculateDurationSeconds(wordCount, script.narrationWordsPerMinute)

        val updatedScript = script.copy(
            wordCount = wordCount,
            estimatedDurationSeconds = durationSeconds,
            updatedAtUtc = System.currentTimeMillis()
        )

        if (sections.isNotEmpty()) {
            scriptDao.replaceScriptWithSections(updatedScript, sections)
        } else {
            scriptDao.insertScript(updatedScript)
        }
    }

    suspend fun createNewScript(
        title: String,
        format: ContentFormat,
        contentItemId: String? = null
    ): ScriptEntity {
        val scriptId = UUID.randomUUID().toString()
        val defaultSections = if (format == ContentFormat.LONG_FORM_VIDEO) {
            listOf(
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "OPENING_HOOK", 0, 1, "Opening Hook & Stakes", "In this video...", "Dynamic fast-paced b-roll", "Title headline", 45),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "CONTEXT", 1, 2, "Context & Problem", "Most creators struggle with...", "Talking head with graphics", "The Core Problem", 90),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "MAIN_SECTION", 2, 3, "Core Framework / Principle", "Step 1: Build the architecture...", "Screen recording demo", "Step 1: Architecture", 300),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "EXAMPLES", 3, 4, "Real-world Case Studies", "Here is how this worked in practice...", "Split screen comparison", "Before vs After", 180),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "CLIMAX", 4, 5, "Key Transformation Insight", "The breakthrough realization...", "Music swell / dramatic zoom", "The Breakthrough", 60),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "CONCLUSION", 5, 6, "Conclusion & Summary", "To recap the 3 pillars...", "Bullet points on screen", "Key Takeaways", 45),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "CTA", 6, 7, "Call To Action", "Check out the full repository in the description.", "End screen elements", "Subscribe & Link in bio", 20)
            )
        } else {
            listOf(
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "HOOK", 0, 1, "The Hook (0-3s)", "Stop making this mistake if you want to grow...", "Punch in / visual disruption", "WATCH THIS", 3),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "CURIOSITY_GAP", 1, 2, "Curiosity Gap (3-8s)", "90% of creators do this wrong because...", "Quick graphic breakdown", "The Mistake", 5),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "EXPLANATION", 2, 3, "Core Insight (8-25s)", "Instead, you need to implement this system...", "Screen demonstration / rapid cuts", "The Fix", 17),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "PROGRESSION", 3, 4, "Visual Progression (25-45s)", "Step 1, Step 2, and Step 3...", "Step-by-step overlays", "Steps 1-3", 20),
                ScriptSectionEntity(UUID.randomUUID().toString(), scriptId, "PAYOFF_CTA", 4, 5, "Payoff & CTA (45-60s)", "Save this video and follow for daily systems.", "Direct eye contact closing", "Follow for more", 15)
            )
        }

        val totalWords = defaultSections.sumOf { it.spokenWords.split("\\s+".toRegex()).filter { w -> w.isNotBlank() }.size }
        val script = ScriptEntity(
            id = scriptId,
            contentItemId = contentItemId,
            title = title,
            format = format,
            hook = defaultSections.firstOrNull()?.spokenWords ?: "",
            scriptBody = defaultSections.joinToString("\n\n") { "## ${it.title}\n${it.spokenWords}" },
            wordCount = totalWords,
            estimatedDurationSeconds = calculateDurationSeconds(totalWords, 140)
        )

        scriptDao.replaceScriptWithSections(script, defaultSections)
        return script
    }

    suspend fun deleteScript(id: String) {
        scriptDao.deleteScript(id)
    }

    private fun calculateWordCount(body: String, sections: List<ScriptSectionEntity>): Int {
        val bodyWords = body.split("\\s+".toRegex()).count { it.isNotBlank() }
        val sectionWords = sections.sumOf { s -> s.spokenWords.split("\\s+".toRegex()).count { it.isNotBlank() } }
        return maxOf(bodyWords, sectionWords)
    }

    fun calculateDurationSeconds(wordCount: Int, wordsPerMinute: Int): Int {
        val wpm = if (wordsPerMinute > 0) wordsPerMinute else 140
        return ((wordCount.toFloat() / wpm) * 60f).toInt()
    }
}
