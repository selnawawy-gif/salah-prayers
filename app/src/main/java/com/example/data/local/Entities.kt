package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_records")
data class PrayerRecordEntity(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    val fajrStatus: String = "NOT_LOGGED",
    val dhuhrStatus: String = "NOT_LOGGED",
    val asrStatus: String = "NOT_LOGGED",
    val maghribStatus: String = "NOT_LOGGED",
    val ishaStatus: String = "NOT_LOGGED",
    val note: String = "",
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_verses")
data class DailyVerseEntity(
    @PrimaryKey
    val id: String,
    val arabicText: String,
    val translation: String,
    val reference: String,
    val theme: String,
    val isFavorite: Boolean = false,
    val isCustomAdded: Boolean = false
)

@Entity(tableName = "supplications")
data class SupplicationEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val title: String,
    val arabicText: String,
    val translation: String,
    val transliteration: String,
    val reference: String,
    val targetCount: Int = 1,
    val virtue: String = "",
    val isCustomAdded: Boolean = false
)

@Entity(tableName = "sync_logs")
data class SyncLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val syncTimestampMillis: Long,
    val syncAction: String,
    val details: String
)
