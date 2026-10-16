package com.example.demo_deploy.pricing.domain

data class PriceQuote(
    val base: Price,
    val surcharge: Price,
    val total: Price,
)
