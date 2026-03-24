package com.example.workmanager.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.workmanager.data.WorkLog
import com.example.workmanager.data.WorkLogDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class ChainWorkerB @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val fromA = inputData.getString("step_a_result") ?: "none"
        Log.d("ChainWorkerB", "Step B started, received from A: $fromA")
        workLogDao.insert(WorkLog(workerName = "ChainWorkerB", status = "RUNNING", message = "Step B: Processing (input from A: $fromA)"))
        delay(1500)
        workLogDao.insert(WorkLog(workerName = "ChainWorkerB", status = "COMPLETED", message = "Step B: Processing complete"))
        return Result.success(workDataOf("step_b_result" to "data_from_B"))
    }
}
