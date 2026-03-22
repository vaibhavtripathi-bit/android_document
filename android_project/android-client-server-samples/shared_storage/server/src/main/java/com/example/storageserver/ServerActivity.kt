package com.example.storageserver

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.storageserver.databinding.ActivityServerBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServerBinding
    private val authority = "com.example.storageserver.fileprovider"
    private var lastCreatedUri: android.net.Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCreateShare.setOnClickListener { createAndShareFile() }
        binding.btnShareIntent.setOnClickListener { shareViaIntent() }

        refreshFileList()
    }

    private fun createAndShareFile(): android.net.Uri? {
        val sharedDir = File(filesDir, "shared").apply { mkdirs() }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(sharedDir, "document_$timestamp.txt")

        val content = buildString {
            appendLine("{")
            appendLine("  \"type\": \"shared_document\",")
            appendLine("  \"created_at\": \"${Date()}\",")
            appendLine("  \"server_package\": \"$packageName\",")
            appendLine("  \"message\": \"This file was created by the Storage Server app and shared via FileProvider.\",")
            appendLine("  \"sequence\": ${sharedDir.listFiles()?.size ?: 0}")
            appendLine("}")
        }
        file.writeText(content)

        val uri = FileProvider.getUriForFile(this, authority, file)
        lastCreatedUri = uri

        grantUriPermission(
            "com.example.storageclient",
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        Toast.makeText(this, "File created & URI permission granted", Toast.LENGTH_SHORT).show()
        refreshFileList()
        return uri
    }

    private fun shareViaIntent() {
        val uri = lastCreatedUri ?: createAndShareFile()
        if (uri == null) {
            Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show()
            return
        }

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(sendIntent, "Share file via"))
    }

    private fun refreshFileList() {
        val sharedDir = File(filesDir, "shared")
        val files = sharedDir.listFiles()

        if (files.isNullOrEmpty()) {
            binding.tvOutput.text = "No files created yet."
            return
        }

        val output = buildString {
            appendLine("Directory: ${sharedDir.absolutePath}")
            appendLine("File count: ${files.size}")
            appendLine("─".repeat(40))

            files.sortedByDescending { it.lastModified() }.forEach { file ->
                val uri = FileProvider.getUriForFile(this@ServerActivity, authority, file)
                appendLine()
                appendLine("📄 ${file.name}")
                appendLine("   Size: ${file.length()} bytes")
                appendLine("   URI: $uri")
                appendLine("   Path: ${file.absolutePath}")
            }

            if (lastCreatedUri != null) {
                appendLine()
                appendLine("─".repeat(40))
                appendLine("Last granted URI:")
                appendLine("  $lastCreatedUri")
                appendLine("  (granted to com.example.storageclient)")
            }
        }
        binding.tvOutput.text = output
    }
}
