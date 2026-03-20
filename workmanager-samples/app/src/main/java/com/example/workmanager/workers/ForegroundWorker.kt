package com.example.workmanager.workers

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.workmanager.WorkManagerApp
import com.example.workmanager.data.WorkLog
import com.example.workmanager.data.WorkLogDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class ForegroundWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Foreground work started")
        setForeground(createForegroundInfo("Starting long-running task..."))
        workLogDao.insert(WorkLog(workerName = "ForegroundWorker", status = "RUNNING", message = "Long-running task started"))

        for (i in 1..10) {
            delay(1000)
            setForeground(createForegroundInfo("Progress: ${i * 10}%"))
            setProgress(androidx.work.workDataOf("progress" to i * 10))
        }

        workLogDao.insert(WorkLog(workerName = "ForegroundWorker", status = "COMPLETED", message = "Long-running task finished"))
        return Result.success()
    }

    private fun createForegroundInfo(progressText: String): ForegroundInfo {
        val notification = NotificationCompat.Builder(applicationContext, WorkManagerApp.CHANNEL_ID)
            .setContentTitle("Long-Running Work")
            .setContentText(progressText)
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .setOngoing(true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val TAG = "ForegroundWorker"
        const val NOTIFICATION_ID = 1001
    }
}
