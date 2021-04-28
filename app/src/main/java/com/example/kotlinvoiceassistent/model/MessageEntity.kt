package com.example.kotlinvoiceassistent.model

import java.text.SimpleDateFormat

class MessageEntity {
    var text: String
        private set
    var date: String
        private set
    var sender: Int
        private set

    constructor(text: String, date: String, sender: Int) {
        this.text = text
        this.date = date
        this.sender = sender
    }

    constructor(message: Message) {
        text = message.text
        date = SimpleDateFormat().format(message.date)
        sender = message.sender.ordinal
    }

}
