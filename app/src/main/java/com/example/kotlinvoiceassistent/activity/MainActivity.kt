package com.example.kotlinvoiceassistent.activity

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinvoiceassistent.R
import com.example.kotlinvoiceassistent.adapter.MessageListAdapter
import com.example.kotlinvoiceassistent.enumeration.Sender
import com.example.kotlinvoiceassistent.model.Message
import com.example.kotlinvoiceassistent.model.MessageEntity
import com.example.kotlinvoiceassistent.service.AI
import com.example.kotlinvoiceassistent.service.DBHelper
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    protected lateinit var sendButton: Button
    protected lateinit var questionText: EditText
    protected lateinit var chatMessageList: RecyclerView
    protected lateinit var textToSpeech: TextToSpeech
    protected lateinit var messageListAdapter: MessageListAdapter
    protected lateinit var questionService: AI

    private lateinit var sPref: SharedPreferences

    private val APP_PREFERENCES = "mysettings"

    private var isLight = true
    private val THEME = "THEME"

    private lateinit var dBHelper: DBHelper
    private lateinit var database: SQLiteDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isLight = sPref.getBoolean(THEME, true)
        setDayNightTheme()
        chatMessageList = findViewById(R.id.chatWindow)
        messageListAdapter = MessageListAdapter()
        chatMessageList.setLayoutManager(LinearLayoutManager(this))
        chatMessageList.setAdapter(messageListAdapter)

        dBHelper = DBHelper(this)
        database = dBHelper.writableDatabase
        database.query(DBHelper.TABLE_MESSAGES, null, null, null, null, null, null)
            .use { cursor ->
                if (cursor.moveToFirst()) {
                    val messageIndex = cursor.getColumnIndex(DBHelper.FIELD_MESSAGE)
                    val dateIndex = cursor.getColumnIndex(DBHelper.FIELD_DATE)
                    val sendIndex = cursor.getColumnIndex(DBHelper.FIELD_SEND)
                    do {
                        val entity = MessageEntity(
                            cursor.getString(messageIndex),
                            cursor.getString(dateIndex), cursor.getInt(sendIndex)
                        )
                        try {
                            messageListAdapter.messageList.add(Message(entity))

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } while (cursor.moveToNext())
                }
            }

        sendButton = findViewById(R.id.sendButton)
        questionText = findViewById(R.id.questionField)
        sendButton.setOnClickListener(View.OnClickListener { v: View? -> onSend() })
        textToSpeech = TextToSpeech(applicationContext, OnInitListener { i: Int ->
            if (i != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.getDefault()
            }
        })
        questionService = AI(this)
        questionService.subscribe(object : Observer<String?> {
            override fun onSubscribe(d: @NonNull Disposable?) {}
            override fun onNext(s: @NonNull String?) {
                messageListAdapter.messageList.add(Message(s!!, Sender.Assistant))
                textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null)
                messageListAdapter.notifyDataSetChanged()
                chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
                sendButton.setEnabled(true)
                questionText.setHint(R.string.question_text)
            }

            override fun onError(e: @NonNull Throwable?) {
                onNext(e!!.message)
            }

            override fun onComplete() {}
        })
        if (!messageListAdapter.messageList.isEmpty()) {
            chatMessageList.scrollToPosition(messageListAdapter.messageList.size - 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        dBHelper.close()
        database.close()
    }

    override fun onOptionsItemSelected(item: @NonNull MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_settings -> isLight = true
            R.id.night_settings -> isLight = false
            else -> {
            }
        }
        setDayNightTheme()
        return super.onOptionsItemSelected(item)
    }

    private fun setDayNightTheme() {
        if (isLight) {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
        } else {
            delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
        }
    }

    override fun onStop() {
        super.onStop()

        sPref.edit().putBoolean(THEME, isLight).apply()
        database.delete(DBHelper.TABLE_MESSAGES, null, null)
        for (message in messageListAdapter.messageList) {
            val messageEntity = MessageEntity(message)
            val contentValues = ContentValues()
            contentValues.put(DBHelper.FIELD_MESSAGE, messageEntity.text)
            contentValues.put(DBHelper.FIELD_DATE, messageEntity.date)
            contentValues.put(DBHelper.FIELD_SEND, messageEntity.sender)
            database.insert(DBHelper.TABLE_MESSAGES, null, contentValues)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("messageHistory", messageListAdapter.messageList)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        messageListAdapter.messageList =
            savedInstanceState.getParcelableArrayList<Message>("messageHistory") as ArrayList<Message>
    }

    protected fun onSend() {
        val text = questionText.text.toString()
        questionText.setText("")
        if (text.isEmpty()) return
        messageListAdapter.messageList.add(Message(text, Sender.User))
        sendButton.isEnabled = false
        questionText.setHint(R.string.thinking_about_it)
        questionService.getAnswer(text)
    }
}