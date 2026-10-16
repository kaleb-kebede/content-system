package com.example.creatorgrowthos.data.repository

import com.example.creatorgrowthos.data.local.dao.ContentDerivativeDao
import com.example.creatorgrowthos.data.local.dao.ContentItemDao
import com.example.creatorgrowthos.data.local.dao.PublicationDao
import com.example.creatorgrowthos.data.local.entity.ContentDerivativeEntity
import com.example.creatorgrowthos.data.local.entity.ContentPublicationEntity
import com.example.creatorgrowthos.domain.model.ContentFormat
import com.example.creatorgrowthos.domain.model.PlatformType
import com.example.creatorgrowthos.domain.model.PublicationStatus
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RepurposingRepository(
    private val derivativeDao: ContentDerivativeDao,
    private val contentItemDao: ContentItemDao,
    private val publicationDao: PublicationDao
) {
    fun getAllDerivatives(): Flow<List<ContentDerivativeEntity>> = derivativeDao.getAllDerivatives()

    fun getDerivativesForMaster(masterId: String): Flow<List<ContentDerivativeEntity>> =
        derivativeDao.getDerivativesForMaster(masterId)

    suspend fun getDerivativeById(id: String): ContentDerivativeEntity? = derivativeDao.getDerivativeById(id)

    /**
     * Creates a derivative from a master content item and prepares a corresponding publication draft.
     */
    suspend fun createDerivative(
        masterContentId: String,
        destination: PlatformType,
        title: String,
        hook: String,
        caption: String,
        hashtags: List<String>,
        editingNotes: String = "",
        plannedPublicationUtc: Long? = null
    ): ContentDerivativeEntity {
        val derivativeId = UUID.randomUUID().toString()
        val publicationId = UUID.randomUUID().toString()

        val derivative = ContentDerivativeEntity(
            id = derivativeId,
            masterContentId = masterContentId,
            title = title,
            destinationPlatform = destination,
            specificHook = hook,
            caption = caption,
            hashtags = hashtags,
            editingNotes = editingNotes,
            plannedPublicationUtc = plannedPublicationUtc,
            publicationId = publicationId
        )
        derivativeDao.insertDerivative(derivative)

        // Automatically create destination-specific publication draft
        if (plannedPublicationUtc != null) {
            val pub = ContentPublicationEntity(
                id = publicationId,
                contentItemId = masterContentId,
                derivativeId = derivativeId,
                destination = destination,
                format = if (destination == PlatformType.YOUTUBE_LONG) ContentFormat.LONG_FORM_VIDEO else ContentFormat.SHORT_FORM_VIDEO,
                platformSpecificTitle = title,
                caption = caption,
                hashtags = hashtags,
                plannedPublicationUtc = plannedPublicationUtc,
                publicationStatus = PublicationStatus.DRAFT
            )
            publicationDao.insertPublication(pub)
        }

        return derivative
    }

    suspend fun deleteDerivative(id: String) {
        derivativeDao.deleteDerivative(id)
    }
}
