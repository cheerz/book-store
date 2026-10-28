package com.example.demo_deploy.it.client

data class BookPayload(
    val title: String,
    val bookKind: String,
    val bookSize: String = "small",
    val priceInCents: Long = 1_000,
)
