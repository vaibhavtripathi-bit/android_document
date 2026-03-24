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
class ExpeditedWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        Log.d(TAG, "Expedited work started — high priority!")
        workLogDao.insert(WorkLog(workerName = "ExpeditedWorker", status = "RUNNING", message = "Expedited: Processing urgent task"))
        delay(1000)
        workLogDao.insert(WorkLog(workerName = "ExpeditedWorker", status = "COMPLETED", message = "Expedited: Urgent task done"))
        return Result.success()
    }

    companion object {
        const val TAG = "ExpeditedWorker"
    }
}
