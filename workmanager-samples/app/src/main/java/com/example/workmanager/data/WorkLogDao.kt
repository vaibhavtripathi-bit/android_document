package com.example.workmanager.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkLogDao {
    @Insert
    suspend fun insert(log: WorkLog)

    @Query("SELECT * FROM work_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<WorkLog>>

    @Query("DELETE FROM work_logs")
    suspend fun clearAll()
}
