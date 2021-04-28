package com.example.kotlinvoiceassistent.model

import android.os.Parcel
import android.os.Parcelable
import com.example.kotlinvoiceassistent.enumeration.Sender
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Message : Parcelable {
    var text: String
    var date: Date
    var sender: Sender

    @Throws(ArrayIndexOutOfBoundsException::class, ParseException::class)
    constructor(entity: MessageEntity) {
        text = entity.text
        date = SimpleDateFormat().parse(entity.date)
        sender = Sender.values()[entity.sender]
    }

    constructor(text: String, sender: Sender) {
        this.text = text
        this.sender = sender
        date = Date()
    }

    constructor(parcel: Parcel) {
        val data = parcel.createStringArray()
        text = data!![0]
        try {
            date = SimpleDateFormat().parse(data[1])
        } catch (e: Exception) {
            date = Date()
        }
        sender = Sender.valueOf(data[2])
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringArray(arrayOf(text, date.toString(), sender.name))
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Message?> = object : Parcelable.Creator<Message?> {
            override fun createFromParcel(source: Parcel): Message? {
                return Message(source)
            }

            override fun newArray(size: Int): Array<Message?> {
                return arrayOfNulls(size)
            }
        }
    }
}