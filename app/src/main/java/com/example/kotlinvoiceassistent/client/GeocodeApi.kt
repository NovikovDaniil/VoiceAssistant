package com.example.kotlinvoiceassistent.client

import com.example.kotlinvoiceassistent.model.Geolocation
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodeApi {
    @GET("/v1/forward?access_key=322b797aa361a3035cfe741120e6e958")
    fun getCityGeolocation(@Query("query") city: String): Observable<Geolocation>
}