package com.example.kotlinvoiceassistent.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Forecast {
    @SerializedName("current")
    @Expose
    val current = Weather()
        get

    class Weather {
        @SerializedName("temperature")
        @Expose
        val temperature = 0

        @SerializedName("weather_descriptions")
        @Expose
        val weatherDescriptions = ArrayList<String>()

    }
}