package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        PrayerRecordEntity::class,
        DailyVerseEntity::class,
        SupplicationEntity::class,
        SyncLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SalahDatabase : RoomDatabase() {
    abstract fun salahDao(): SalahDao

    companion object {
        @Volatile
        private var INSTANCE: SalahDatabase? = null

        fun getInstance(context: Context): SalahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SalahDatabase::class.java,
                    "salah_app_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
