package com.example.bookstore.format.domain

data class Format(
    val code: String,
    val label: String,
    val surcharge: Price,
)
