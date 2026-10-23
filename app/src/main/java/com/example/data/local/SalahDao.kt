package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SalahDao {
    // Prayer records
    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    suspend fun getPrayerRecordForDate(date: String): PrayerRecordEntity?

    @Query("SELECT * FROM prayer_records WHERE date = :date LIMIT 1")
    fun observePrayerRecordForDate(date: String): Flow<PrayerRecordEntity?>

    @Query("SELECT * FROM prayer_records ORDER BY date DESC LIMIT 30")
    fun observeRecentPrayerRecords(): Flow<List<PrayerRecordEntity>>

    @Query("SELECT * FROM prayer_records ORDER BY date DESC")
    suspend fun getAllPrayerRecords(): List<PrayerRecordEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePrayerRecord(record: PrayerRecordEntity)

    // Daily verses
    @Query("SELECT * FROM daily_verses")
    fun observeAllVerses(): Flow<List<DailyVerseEntity>>

    @Query("SELECT * FROM daily_verses")
    suspend fun getAllVerses(): List<DailyVerseEntity>

    @Query("SELECT * FROM daily_verses WHERE id = :id LIMIT 1")
    suspend fun getVerseById(id: String): DailyVerseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerses(verses: List<DailyVerseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVerse(verse: DailyVerseEntity)

    @Update
    suspend fun updateVerse(verse: DailyVerseEntity)

    // Supplications
    @Query("SELECT * FROM supplications")
    fun observeAllSupplications(): Flow<List<SupplicationEntity>>

    @Query("SELECT * FROM supplications WHERE category = :category")
    fun observeSupplicationsByCategory(category: String): Flow<List<SupplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplications(items: List<SupplicationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplication(item: SupplicationEntity)

    // Sync logs
    @Query("SELECT * FROM sync_logs ORDER BY syncTimestampMillis DESC LIMIT 1")
    fun observeLatestSyncLog(): Flow<SyncLogEntity?>

    @Insert
    suspend fun insertSyncLog(log: SyncLogEntity)
}
