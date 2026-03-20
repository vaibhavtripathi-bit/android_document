package com.example.cpclient

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cpclient.databinding.ActivityClientBinding

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientBinding

    private val contentUri: Uri = Uri.parse("content://com.example.cpserver.notes/notes")

    private var insertCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnQueryAll.setOnClickListener { queryAll() }
        binding.btnInsert.setOnClickListener { insertNote() }
        binding.btnUpdateFirst.setOnClickListener { updateFirst() }
        binding.btnDeleteFirst.setOnClickListener { deleteFirst() }
    }

    private fun queryAll() {
        try {
            val cursor = contentResolver.query(contentUri, null, null, null, null)
            if (cursor == null) {
                appendResult("Query returned null cursor")
                return
            }

            val sb = StringBuilder("=== Query All (${cursor.count} rows) ===\n")
            val idIdx = cursor.getColumnIndex("_id")
            val titleIdx = cursor.getColumnIndex("title")
            val bodyIdx = cursor.getColumnIndex("body")
            val createdIdx = cursor.getColumnIndex("created_at")

            while (cursor.moveToNext()) {
                sb.append("ID=${cursor.getLong(idIdx)}")
                sb.append(" | title=${cursor.getString(titleIdx)}")
                sb.append(" | body=${cursor.getString(bodyIdx)}")
                sb.append(" | created=${cursor.getLong(createdIdx)}")
                sb.append("\n")
            }
            cursor.close()
            appendResult(sb.toString())
        } catch (e: SecurityException) {
            appendResult("SecurityException: ${e.message}")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            appendResult("Error: ${e.message}")
        }
    }

    private fun insertNote() {
        try {
            insertCounter++
            val values = ContentValues().apply {
                put("title", "Note #$insertCounter")
                put("body", "Body of note #$insertCounter from client app")
            }
            val resultUri = contentResolver.insert(contentUri, values)
            val msg = "Inserted: $resultUri"
            appendResult(msg)
            Toast.makeText(this, "Insert OK", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            appendResult("SecurityException: ${e.message}")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            appendResult("Error: ${e.message}")
        }
    }

    private fun updateFirst() {
        try {
            val cursor = contentResolver.query(
                contentUri, arrayOf("_id"), null, null, "_id ASC LIMIT 1"
            )
            if (cursor == null || !cursor.moveToFirst()) {
                appendResult("No notes to update")
                cursor?.close()
                return
            }
            val firstId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            cursor.close()

            val values = ContentValues().apply {
                put("title", "Updated @ ${System.currentTimeMillis()}")
            }
            val count = contentResolver.update(
                contentUri, values, "_id = ?", arrayOf(firstId.toString())
            )
            appendResult("Updated $count row(s) (ID=$firstId)")
            Toast.makeText(this, "Update OK", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            appendResult("SecurityException: ${e.message}")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            appendResult("Error: ${e.message}")
        }
    }

    private fun deleteFirst() {
        try {
            val cursor = contentResolver.query(
                contentUri, arrayOf("_id"), null, null, "_id ASC LIMIT 1"
            )
            if (cursor == null || !cursor.moveToFirst()) {
                appendResult("No notes to delete")
                cursor?.close()
                return
            }
            val firstId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"))
            cursor.close()

            val count = contentResolver.delete(
                contentUri, "_id = ?", arrayOf(firstId.toString())
            )
            appendResult("Deleted $count row(s) (ID=$firstId)")
            Toast.makeText(this, "Delete OK", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            appendResult("SecurityException: ${e.message}")
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            appendResult("Error: ${e.message}")
        }
    }

    private fun appendResult(text: String) {
        binding.tvResults.append("$text\n\n")
    }
}
