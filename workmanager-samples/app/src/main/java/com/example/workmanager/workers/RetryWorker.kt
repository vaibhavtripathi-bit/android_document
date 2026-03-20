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

@HiltWorker
class RetryWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Retry worker attempt: $runAttemptCount")
        workLogDao.insert(WorkLog(workerName = "RetryWorker", status = "ATTEMPT", message = "Attempt #$runAttemptCount"))

        return if (runAttemptCount < 3) {
            Log.d(TAG, "Simulating failure, will retry")
            workLogDao.insert(WorkLog(workerName = "RetryWorker", status = "RETRY", message = "Failed attempt #$runAttemptCount, retrying..."))
            Result.retry()
        } else {
            Log.d(TAG, "Succeeded on attempt $runAttemptCount")
            workLogDao.insert(WorkLog(workerName = "RetryWorker", status = "COMPLETED", message = "Succeeded on attempt #$runAttemptCount"))
            Result.success()
        }
    }

    companion object {
        const val TAG = "RetryWorker"
    }
}
