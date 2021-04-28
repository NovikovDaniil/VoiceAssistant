package com.example.kotlinvoiceassistent.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class NumberString {
    @SerializedName("str")
    @Expose
    var numberString: String = ""
}