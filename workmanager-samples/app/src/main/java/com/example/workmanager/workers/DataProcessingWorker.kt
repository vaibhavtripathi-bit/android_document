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
class DataProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workLogDao: WorkLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val inputText = inputData.getString(KEY_INPUT_TEXT) ?: ""
        val operation = inputData.getString(KEY_OPERATION) ?: "uppercase"

        Log.d(TAG, "Processing: operation=$operation, input='$inputText'")
        workLogDao.insert(WorkLog(workerName = "DataProcessingWorker", status = "RUNNING", message = "Op: $operation on '$inputText'"))

        delay(1000)

        val result = when (operation) {
            "uppercase" -> inputText.uppercase()
            "reverse" -> inputText.reversed()
            "word_count" -> "${inputText.split(" ").size} words"
            "char_count" -> "${inputText.length} chars"
            else -> inputText
        }

        workLogDao.insert(WorkLog(workerName = "DataProcessingWorker", status = "COMPLETED", message = "Result: $result"))
        return Result.success(workDataOf(KEY_OUTPUT_TEXT to result))
    }

    companion object {
        const val TAG = "DataProcessingWorker"
        const val KEY_INPUT_TEXT = "input_text"
        const val KEY_OPERATION = "operation"
        const val KEY_OUTPUT_TEXT = "output_text"
    }
}
