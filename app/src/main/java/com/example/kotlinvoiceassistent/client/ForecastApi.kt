package com.example.kotlinvoiceassistent.client

import com.example.kotlinvoiceassistent.model.Forecast
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastApi {
    @GET("/current?access_key=c68ef554d3d93855c46167997e0e4708")
    fun getCurrentWeather(@Query("query") city: String): Observable<Forecast>
}