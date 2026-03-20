package com.example.aidlclient

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aidlclient.databinding.ActivityClientBinding
import com.example.aidlserver.Book
import com.example.aidlserver.IBookManager

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientBinding
    private var bookManager: IBookManager? = null
    private var isBound = false
    private var nextBookId = 100

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            bookManager = IBookManager.Stub.asInterface(service)
            isBound = true
            binding.statusText.text = "Connected to AIDL BookManager Service"
            refreshBookList()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bookManager = null
            isBound = false
            binding.statusText.text = "Disconnected from service"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBind.setOnClickListener { bindToService() }
        binding.btnUnbind.setOnClickListener { unbindFromService() }
        binding.btnGetBooks.setOnClickListener { refreshBookList() }
        binding.btnAddBook.setOnClickListener { addBook() }
    }

    private fun bindToService() {
        val intent = Intent("com.example.aidlserver.IBookManager").apply {
            setPackage("com.example.aidlserver")
        }
        val bound = bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (!bound) {
            binding.statusText.text = "Failed to bind — is the server app installed?"
        }
    }

    private fun unbindFromService() {
        if (isBound) {
            unbindService(connection)
            isBound = false
            bookManager = null
            binding.statusText.text = "Unbound from service"
            binding.bookListText.text = ""
        }
    }

    private fun refreshBookList() {
        if (!isBound) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val books = bookManager?.bookList ?: emptyList()
            binding.bookListText.text = books.joinToString("\n") { "• [${it.id}] ${it.title} by ${it.author}" }
        } catch (e: RemoteException) {
            binding.bookListText.text = "Error: ${e.message}"
        }
    }

    private fun addBook() {
        if (!isBound) {
            Toast.makeText(this, "Not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val book = Book(nextBookId++, "New Book #${nextBookId - 1}", "Client Author")
            bookManager?.addBook(book)
            refreshBookList()
            Toast.makeText(this, "Added: ${book.title}", Toast.LENGTH_SHORT).show()
        } catch (e: RemoteException) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        unbindFromService()
        super.onDestroy()
    }
}
