package com.example.demo_deploy.pricing.domain

interface PriceManager {
    fun quote(basePriceInCents: Long, productType: String): PriceQuote
}
