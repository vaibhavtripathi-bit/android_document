package com.example.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.workmanager.data.WorkLog
import com.example.workmanager.data.WorkLogDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class PeriodicSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Periodic sync started, run attempt: $runAttemptCount")
        workLogDao.insert(WorkLog(workerName = "PeriodicSyncWorker", status = "RUNNING", message = "Sync attempt #$runAttemptCount"))

        delay(1500)

        workLogDao.insert(WorkLog(workerName = "PeriodicSyncWorker", status = "COMPLETED", message = "Sync completed"))
        return Result.success()
    }

    companion object {
        const val TAG = "PeriodicSyncWorker"
        const val UNIQUE_NAME = "periodic_sync"
    }
}
