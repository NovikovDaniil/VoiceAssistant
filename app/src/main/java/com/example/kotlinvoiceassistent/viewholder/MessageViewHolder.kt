package com.example.kotlinvoiceassistent.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinvoiceassistent.R
import com.example.kotlinvoiceassistent.model.Message
import java.text.DateFormat
import java.text.SimpleDateFormat

class MessageViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    protected var messageText: TextView
    protected var messageDate: TextView
    fun bind(message: Message) {
        messageText.text = message.text
        val fmt: DateFormat = SimpleDateFormat()
        messageDate.text = fmt.format(message.date)
    }

    init {
        messageText = itemView.findViewById(R.id.messageTextView)
        messageDate = itemView.findViewById(R.id.messageDateView)
    }
}