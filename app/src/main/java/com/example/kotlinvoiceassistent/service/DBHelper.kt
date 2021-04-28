package com.example.kotlinvoiceassistent.service

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE " + TABLE_MESSAGES + "(" +
                    FIELD_ID + " INTEGER PRIMARY KEY," +
                    FIELD_MESSAGE + " TEXT," +
                    FIELD_SEND + " INTEGER," +
                    FIELD_DATE + " TEXT" + ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "message_db"
        const val TABLE_MESSAGES = "messages"
        const val FIELD_ID = "id"
        const val FIELD_MESSAGE = "message"
        const val FIELD_SEND = "send"
        const val FIELD_DATE = "date"
    }
}