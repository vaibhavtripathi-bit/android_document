package com.example.cpserver

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cpserver.databinding.ActivityServerBinding

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        refreshCount()
    }

    override fun onResume() {
        super.onResume()
        refreshCount()
    }

    private fun refreshCount() {
        val cursor = contentResolver.query(
            NoteContract.CONTENT_URI, null, null, null, null
        )
        val count = cursor?.count ?: 0
        cursor?.close()
        binding.tvStatus.text = "ContentProvider is running.\nNotes in database: $count"
    }
}
