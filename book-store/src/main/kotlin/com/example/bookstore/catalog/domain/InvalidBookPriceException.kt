package com.example.bookstore.catalog.domain

class InvalidBookPriceException(
    val receivedPriceInCents: Long,
) : RuntimeException("A book price cannot be negative: $receivedPriceInCents")
