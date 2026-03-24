package com.example.cpserver

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.os.Binder
import android.util.Log

class NoteProvider : ContentProvider() {

    companion object {
        private const val TAG = "NoteProvider"
        private const val DB_NAME = "notes.db"
        private const val DB_VERSION = 1
        private const val TABLE_NOTES = "notes"

        private const val NOTES_DIR = 1
        private const val NOTES_ITEM = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(NoteContract.AUTHORITY, "notes", NOTES_DIR)
            addURI(NoteContract.AUTHORITY, "notes/#", NOTES_ITEM)
        }
    }

    private lateinit var dbHelper: NoteDbHelper

    private class NoteDbHelper(context: android.content.Context) :
        SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE $TABLE_NOTES (
                    ${NoteContract.Columns.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                    ${NoteContract.Columns.TITLE} TEXT NOT NULL,
                    ${NoteContract.Columns.BODY} TEXT,
                    ${NoteContract.Columns.CREATED_AT} INTEGER NOT NULL DEFAULT (strftime('%s','now'))
                )
                """.trimIndent()
            )
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
            onCreate(db)
        }
    }

    override fun onCreate(): Boolean {
        dbHelper = NoteDbHelper(context ?: return false)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        Log.d(TAG, "query() from PID=${Binder.getCallingPid()}, UID=${Binder.getCallingUid()}")

        val qb = SQLiteQueryBuilder().apply {
            tables = TABLE_NOTES
        }

        when (uriMatcher.match(uri)) {
            NOTES_DIR -> { /* no extra filter */ }
            NOTES_ITEM -> qb.appendWhere("${NoteContract.Columns.ID} = ${ContentUris.parseId(uri)}")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        val order = sortOrder ?: "${NoteContract.Columns.CREATED_AT} DESC"
        val db = dbHelper.readableDatabase
        val cursor = qb.query(db, projection, selection, selectionArgs, null, null, order)
        cursor.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert() from PID=${Binder.getCallingPid()}, UID=${Binder.getCallingUid()}")

        if (uriMatcher.match(uri) != NOTES_DIR) {
            throw IllegalArgumentException("Cannot insert into URI: $uri")
        }

        val db = dbHelper.writableDatabase
        val rowId = db.insert(TABLE_NOTES, null, values)
        if (rowId == -1L) {
            Log.e(TAG, "Failed to insert row into $uri")
            return null
        }

        val newUri = ContentUris.withAppendedId(NoteContract.CONTENT_URI, rowId)
        context?.contentResolver?.notifyChange(newUri, null)
        return newUri
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "update() from PID=${Binder.getCallingPid()}, UID=${Binder.getCallingUid()}")

        val db = dbHelper.writableDatabase
        val count: Int

        when (uriMatcher.match(uri)) {
            NOTES_DIR -> {
                count = db.update(TABLE_NOTES, values, selection, selectionArgs)
            }
            NOTES_ITEM -> {
                val id = ContentUris.parseId(uri)
                val itemSelection = "${NoteContract.Columns.ID} = $id" +
                    if (!selection.isNullOrEmpty()) " AND ($selection)" else ""
                count = db.update(TABLE_NOTES, values, itemSelection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d(TAG, "delete() from PID=${Binder.getCallingPid()}, UID=${Binder.getCallingUid()}")

        val db = dbHelper.writableDatabase
        val count: Int

        when (uriMatcher.match(uri)) {
            NOTES_DIR -> {
                count = db.delete(TABLE_NOTES, selection, selectionArgs)
            }
            NOTES_ITEM -> {
                val id = ContentUris.parseId(uri)
                val itemSelection = "${NoteContract.Columns.ID} = $id" +
                    if (!selection.isNullOrEmpty()) " AND ($selection)" else ""
                count = db.delete(TABLE_NOTES, itemSelection, selectionArgs)
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (count > 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return count
    }

    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            NOTES_DIR -> "vnd.android.cursor.dir/vnd.${NoteContract.AUTHORITY}.notes"
            NOTES_ITEM -> "vnd.android.cursor.item/vnd.${NoteContract.AUTHORITY}.notes"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}
