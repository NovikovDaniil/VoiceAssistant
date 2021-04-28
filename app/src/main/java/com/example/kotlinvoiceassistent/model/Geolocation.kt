package com.example.kotlinvoiceassistent.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Geolocation {
    @SerializedName("data")
    @Expose
    val data = ArrayList<Coordinates>()
            get

    class Coordinates {
        @SerializedName("latitude")
        @Expose
        val latitude = 0.0

        @SerializedName("longitude")
        @Expose
        val longitude = 0.0
    }
}