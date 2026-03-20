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
class ChainWorkerC @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fromB = inputData.getString("step_b_result") ?: "none"
        Log.d("ChainWorkerC", "Step C started, received from B: $fromB")
        workLogDao.insert(WorkLog(workerName = "ChainWorkerC", status = "RUNNING", message = "Step C: Uploading (input from B: $fromB)"))
        delay(1500)
        workLogDao.insert(WorkLog(workerName = "ChainWorkerC", status = "COMPLETED", message = "Step C: Upload complete"))
        return Result.success()
    }
}
