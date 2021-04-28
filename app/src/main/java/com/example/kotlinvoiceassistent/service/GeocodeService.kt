package com.example.kotlinvoiceassistent.service

import com.example.kotlinvoiceassistent.client.GeocodeApi
import com.example.kotlinvoiceassistent.model.Geolocation
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object GeocodeService {
    private val api: GeocodeApi
        private get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://api.positionstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
            return retrofit.create(GeocodeApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
        }

    fun getGeolocation(city: String): Observable<Geolocation> {
        return api.getCityGeolocation(city)
    }
}