package com.example.bookstore.format.domain

data class Price(
    val amountInCents: Long,
) {
    init {
        if (amountInCents < 0) throw InvalidPriceException(amountInCents)
    }
}
