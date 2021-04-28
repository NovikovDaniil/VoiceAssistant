package com.example.kotlinvoiceassistent.service

import com.example.kotlinvoiceassistent.client.NumberStringApi
import com.example.kotlinvoiceassistent.model.NumberString
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object NumberStringService {
    private val api: NumberStringApi
        private get() {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://htmlweb.ru") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
            return retrofit.create(NumberStringApi::class.java) //Создание объекта, при помощи которого будут выполняться запросы
        }

    fun getNumberString(number: String): Observable<NumberString> {
        return api.getNumberString(number)
    }
}