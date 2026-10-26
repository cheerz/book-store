package com.example.bookstore.pricing.domain

interface PriceManager {
    fun quote(
        basePriceInCents: Long,
        productType: ProductType,
    ): PriceQuote

    fun surchargeFor(productType: ProductType): Price
}
