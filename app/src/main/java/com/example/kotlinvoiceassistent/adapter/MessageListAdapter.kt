package com.example.kotlinvoiceassistent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinvoiceassistent.viewholder.MessageViewHolder
import com.example.kotlinvoiceassistent.R
import com.example.kotlinvoiceassistent.enumeration.Sender
import com.example.kotlinvoiceassistent.model.Message
import kotlin.collections.ArrayList

class MessageListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var messageList = ArrayList<Message>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view: View

        //создание сообщения от пользователя
        view = if (viewType == Sender.User.ordinal) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_message, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.assistant_message, parent, false)
        }
        return MessageViewHolder(view)
    }



    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val messageViewHolder: MessageViewHolder = holder as MessageViewHolder
        messageViewHolder.bind(messageList[position])
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(index: Int): Int {
        val message = messageList[index]
        return message.sender.ordinal
    }
}
