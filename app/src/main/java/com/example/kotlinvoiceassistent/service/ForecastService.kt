package com.example.kotlinvoiceassistent.service

import com.example.kotlinvoiceassistent.client.ForecastApi
import com.example.kotlinvoiceassistent.model.Forecast
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ForecastService {
    private val api: ForecastApi
        get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://api.weatherstack.com") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
            return retrofit.create(ForecastApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
        }

    fun getForecast(city: String): Observable<Forecast> {
        return api.getCurrentWeather(city)
    }
}