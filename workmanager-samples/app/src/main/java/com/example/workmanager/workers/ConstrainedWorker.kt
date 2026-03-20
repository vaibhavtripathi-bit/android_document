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
class ConstrainedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val constraintType = inputData.getString(KEY_CONSTRAINT_TYPE) ?: "unknown"
        Log.d(TAG, "Constrained work running with constraint: $constraintType")

        workLogDao.insert(WorkLog(workerName = "ConstrainedWorker", status = "RUNNING", message = "Constraint: $constraintType"))
        delay(2000)
        workLogDao.insert(WorkLog(workerName = "ConstrainedWorker", status = "COMPLETED", message = "Constraint: $constraintType satisfied & completed"))

        return Result.success()
    }

    companion object {
        const val TAG = "ConstrainedWorker"
        const val KEY_CONSTRAINT_TYPE = "constraint_type"
    }
}
