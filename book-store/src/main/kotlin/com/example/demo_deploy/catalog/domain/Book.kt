package com.example.demo_deploy.catalog.domain

data class Book(
    val title: String,
    val kind: BookKind,
    val size: BookSize,
    val priceInCents: Long,
) {
    init {
        if (priceInCents < 0) throw InvalidBookPriceException(priceInCents)
    }
}
