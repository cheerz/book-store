package com.example.bookstore.catalog.domain

data class Book(
    val title: String,
    val kind: BookKind,
    val format: BookFormat,
    val priceInCents: Long,
    val id: Long? = null,
) {
    init {
        if (priceInCents < 0) throw InvalidBookPriceException(priceInCents)
    }
}
