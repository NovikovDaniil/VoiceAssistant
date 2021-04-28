package com.example.kotlinvoiceassistent.model

data class Movie (val name: String, val rating: String) {
    override fun toString(): String {
        return "Название: $name, рейтинг: $rating"
    }
}