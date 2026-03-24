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
class ProgressWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val totalSteps = inputData.getInt(KEY_TOTAL_STEPS, 10)
        Log.d(TAG, "Progress worker started with $totalSteps steps")
        workLogDao.insert(WorkLog(workerName = "ProgressWorker", status = "RUNNING", message = "Starting $totalSteps steps"))

        for (step in 1..totalSteps) {
            delay(500)
            val progress = (step * 100) / totalSteps
            setProgress(workDataOf(KEY_PROGRESS to progress, KEY_CURRENT_STEP to step))
            Log.d(TAG, "Progress: $progress% (step $step/$totalSteps)")
        }

        workLogDao.insert(WorkLog(workerName = "ProgressWorker", status = "COMPLETED", message = "All $totalSteps steps done"))
        return Result.success(workDataOf(KEY_PROGRESS to 100))
    }

    companion object {
        const val TAG = "ProgressWorker"
        const val KEY_TOTAL_STEPS = "total_steps"
        const val KEY_PROGRESS = "progress"
        const val KEY_CURRENT_STEP = "current_step"
    }
}
