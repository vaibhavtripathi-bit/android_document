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
class SimpleWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskName = inputData.getString(KEY_TASK_NAME) ?: "Unnamed Task"
        Log.d(TAG, "Starting simple work: $taskName")

        workLogDao.insert(WorkLog(workerName = "SimpleWorker", status = "STARTED", message = "Task: $taskName"))

        delay(2000)

        workLogDao.insert(WorkLog(workerName = "SimpleWorker", status = "COMPLETED", message = "Task: $taskName finished"))

        val outputData = workDataOf(
            KEY_RESULT_MESSAGE to "Completed '$taskName' at ${System.currentTimeMillis()}",
            KEY_RESULT_TIMESTAMP to System.currentTimeMillis()
        )
        return Result.success(outputData)
    }

    companion object {
        const val TAG = "SimpleWorker"
        const val KEY_TASK_NAME = "task_name"
        const val KEY_RESULT_MESSAGE = "result_message"
        const val KEY_RESULT_TIMESTAMP = "result_timestamp"
    }
}
