package com.example.aidlserver

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList

class BookManagerService : Service() {

    private val books = CopyOnWriteArrayList<Book>().apply {
        add(Book(1, "Effective Kotlin", "Marcin Moskala"))
        add(Book(2, "Android Internals", "Jonathan Levin"))
        add(Book(3, "Kotlin in Action", "Dmitry Jemerov"))
    }

    private val binder = object : IBookManager.Stub() {

        override fun getBookList(): List<Book> {
            Log.d(TAG, "getBookList() called by PID=${getCallingPid()}, UID=${getCallingUid()}")
            return books.toList()
        }

        override fun addBook(book: Book) {
            Log.d(TAG, "addBook($book) called by PID=${getCallingPid()}, UID=${getCallingUid()}")
            books.add(book)
        }

        override fun getBook(id: Int): Book? {
            Log.d(TAG, "getBook($id) called")
            return books.find { it.id == id }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind() — client connected")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind() — client disconnected")
        return super.onUnbind(intent)
    }

    companion object {
        private const val TAG = "BookManagerService"
    }
}
