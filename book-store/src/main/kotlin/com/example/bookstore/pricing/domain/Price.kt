package com.example.bookstore.pricing.domain

data class Price(
    val amountInCents: Long,
) {
    init {
        if (amountInCents < 0) throw InvalidPriceException(amountInCents)
    }

    operator fun plus(other: Price): Price = Price(amountInCents + other.amountInCents)
}
