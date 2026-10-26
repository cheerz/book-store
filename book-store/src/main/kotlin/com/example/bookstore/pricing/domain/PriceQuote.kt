package com.example.bookstore.pricing.domain

data class PriceQuote(
    val base: Price,
    val surcharge: Price,
    val total: Price,
)
