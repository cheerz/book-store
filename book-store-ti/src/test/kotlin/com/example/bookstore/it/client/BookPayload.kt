package com.example.bookstore.it.client

data class BookPayload(
    val title: String,
    val kind: String,
    val format: String = "e-book",
    val bookPriceInCents: Long = 1_000,
)
