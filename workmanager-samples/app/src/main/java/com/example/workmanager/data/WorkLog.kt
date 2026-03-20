package com.example.workmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "work_logs")
data class WorkLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerName: String,
    val status: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
