package com.example.kotlinvoiceassistent.client

import com.example.kotlinvoiceassistent.model.NumberString
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NumberStringApi {
    @GET("/json/convert/num2str?dec=0")
    fun getNumberString(@Query("num") number: String): Observable<NumberString>
}