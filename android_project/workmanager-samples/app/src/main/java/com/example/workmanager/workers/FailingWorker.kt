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
class FailingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val shouldFail = inputData.getBoolean(KEY_SHOULD_FAIL, true)
        Log.d(TAG, "FailingWorker started, shouldFail=$shouldFail")
        workLogDao.insert(WorkLog(workerName = "FailingWorker", status = "RUNNING", message = "Will ${if (shouldFail) "fail" else "succeed"}"))

        delay(2000)

        return if (shouldFail) {
            workLogDao.insert(WorkLog(workerName = "FailingWorker", status = "FAILED", message = "Intentional failure"))
            Result.failure(workDataOf(KEY_ERROR_MESSAGE to "Simulated error"))
        } else {
            workLogDao.insert(WorkLog(workerName = "FailingWorker", status = "COMPLETED", message = "Succeeded"))
            Result.success()
        }
    }

    companion object {
        const val TAG = "FailingWorker"
        const val KEY_SHOULD_FAIL = "should_fail"
        const val KEY_ERROR_MESSAGE = "error_message"
    }
}
