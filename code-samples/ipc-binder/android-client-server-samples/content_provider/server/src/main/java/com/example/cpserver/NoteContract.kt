package com.example.cpserver

import android.net.Uri

object NoteContract {
    const val AUTHORITY = "com.example.cpserver.notes"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/notes")

    object Columns {
        const val ID = "_id"
        const val TITLE = "title"
        const val BODY = "body"
        const val CREATED_AT = "created_at"
    }
}
