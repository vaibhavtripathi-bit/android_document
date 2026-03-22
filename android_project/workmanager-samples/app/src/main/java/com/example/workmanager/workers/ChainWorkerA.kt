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
class ChainWorkerA @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d("ChainWorkerA", "Step A started")
        workLogDao.insert(WorkLog(workerName = "ChainWorkerA", status = "RUNNING", message = "Step A: Downloading data"))
        delay(1500)
        workLogDao.insert(WorkLog(workerName = "ChainWorkerA", status = "COMPLETED", message = "Step A: Download complete"))
        return Result.success(workDataOf("step_a_result" to "data_from_A"))
    }
}
