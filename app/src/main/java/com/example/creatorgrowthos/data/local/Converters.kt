package com.example.creatorgrowthos.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return if (value == null) "[]" else json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrBlank()) emptyList() else try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String {
        return if (value == null) "{}" else json.encodeToString(value)
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String> {
        return if (value.isNullOrBlank()) emptyMap() else try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
