package com.example.kotlinvoiceassistent.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.kotlinvoiceassistent.model.Movie
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDate
import java.util.stream.Collectors

object ParsingHtmlService {
    private val holidayUrl = "http://mirkosmosa.ru/holiday/"
    private val filmUrl = "https://www.kinopoisk.ru/afisha/new/city/505/";

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getHoliday(localDate: LocalDate): String {
        return try {
            val body =
                Jsoup.connect(holidayUrl + localDate.year).get().body()
            val elements = body.select(".month_row")
            elements[localDate.dayOfYear - 1]
                .select(".month_cel li a")
                .stream()
                .map { obj: Element -> obj.text() }
                .collect(Collectors.joining(", "))
        } catch (e: Exception) {
            throw Exception()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(Exception::class)
    fun getLatestMovies(): MutableList<Movie> {
        return try {
            val body =
                Jsoup.connect(filmUrl).get().body()
            val elements = body.select(".filmsListNew .item")
            elements
                .stream()
                .map {
                    val name = it.select("div.poster img").first().attr("alt")
                    var rating: String = "";
                    try {
                        rating = it.select(".rating span").first().text()
                    } catch (ex: Exception) {
                        rating = "-";
                    }

                    Movie(name, rating)
                }
                .collect(Collectors.toList())
        } catch (e: Exception) {
            throw Exception()
        }
    }
}