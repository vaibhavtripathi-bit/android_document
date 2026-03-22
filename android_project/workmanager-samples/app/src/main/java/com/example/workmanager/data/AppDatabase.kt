package com.example.workmanager.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WorkLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workLogDao(): WorkLogDao
}
