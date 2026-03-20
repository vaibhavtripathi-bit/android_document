package com.example.storageclient

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.storageclient.databinding.ActivityClientBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientBinding

    private val openDocumentLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            readAndDisplayUri(uri)
        } else {
            appendOutput("Document picker cancelled.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenDocument.setOnClickListener { openDocument() }
        binding.btnReadKnownUri.setOnClickListener { readKnownUri() }

        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        if (intent.action != Intent.ACTION_SEND) return

        @Suppress("DEPRECATION")
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }

        if (uri != null) {
            appendOutput("Received file via ACTION_SEND:")
            appendOutput("URI: $uri")
            readAndDisplayUri(uri)
        } else if (intent.data != null) {
            appendOutput("Received file via intent data:")
            appendOutput("URI: ${intent.data}")
            readAndDisplayUri(intent.data!!)
        }
    }

    private fun openDocument() {
        openDocumentLauncher.launch(arrayOf("text/*"))
    }

    private fun readKnownUri() {
        val knownUri = Uri.parse(
            "content://com.example.storageserver.fileprovider/shared_files/"
        )

        appendOutput("Attempting to list files from server's FileProvider...")
        appendOutput("Base URI: $knownUri")
        appendOutput("")

        try {
            val cursor = contentResolver.query(knownUri, null, null, null, null)
            if (cursor != null) {
                appendOutput("Query returned ${cursor.count} rows")
                val cols = cursor.columnNames.joinToString(", ")
                appendOutput("Columns: $cols")

                while (cursor.moveToNext()) {
                    val displayName = try {
                        cursor.getString(
                            cursor.getColumnIndexOrThrow("_display_name")
                        )
                    } catch (_: Exception) { "unknown" }
                    appendOutput("  File: $displayName")

                    val fileUri = Uri.withAppendedPath(knownUri, displayName)
                    readAndDisplayUri(fileUri)
                }
                cursor.close()
            } else {
                appendOutput("Query returned null — no access or no files.")
                appendOutput("")
                appendOutput("Tip: First run 'Create & Share File' in the Server app")
                appendOutput("to grant URI permission to this client.")
            }
        } catch (e: SecurityException) {
            appendOutput("⚠ SecurityException: URI permission not granted.")
            appendOutput("  ${e.message}")
            appendOutput("")
            appendOutput("To fix: Open the Server app and tap 'Create & Share File'.")
            appendOutput("The server grants read permission to this package")
            appendOutput("(com.example.storageclient) via grantUriPermission().")
        } catch (e: Exception) {
            appendOutput("⚠ Error: ${e.javaClass.simpleName}")
            appendOutput("  ${e.message}")
        }
    }

    private fun readAndDisplayUri(uri: Uri) {
        appendOutput("─".repeat(40))
        appendOutput("Reading: $uri")

        try {
            val mimeType = contentResolver.getType(uri)
            appendOutput("MIME type: ${mimeType ?: "unknown"}")

            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()

                appendOutput("Size: ${content.length} chars")
                appendOutput("")
                appendOutput("Content:")
                appendOutput(content)
            } ?: run {
                appendOutput("⚠ openInputStream returned null")
            }
        } catch (e: SecurityException) {
            appendOutput("⚠ SecurityException: Cannot read this URI.")
            appendOutput("  ${e.message}")
            appendOutput("")
            appendOutput("The server must grant read permission first.")
        } catch (e: Exception) {
            appendOutput("⚠ Error reading file: ${e.javaClass.simpleName}")
            appendOutput("  ${e.message}")
        }

        appendOutput("─".repeat(40))
        appendOutput("")
    }

    private fun appendOutput(text: String) {
        val current = binding.tvOutput.text.toString()
        val prefix = if (current.startsWith("No file read yet")) "" else "$current\n"
        binding.tvOutput.text = "$prefix$text"
    }
}
