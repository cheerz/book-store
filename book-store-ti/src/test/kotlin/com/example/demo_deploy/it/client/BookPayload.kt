package com.example.demo_deploy.it.client

data class BookPayload(
    val title: String,
    val bookKind: String,
    val bookFormat: String = "e-book",
    val priceInCents: Long = 1_000,
)
