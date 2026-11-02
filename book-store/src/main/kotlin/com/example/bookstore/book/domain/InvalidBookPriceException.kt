package com.example.bookstore.book.domain

class InvalidBookPriceException(
    val receivedPriceInCents: Long,
) : RuntimeException("A book price cannot be negative: $receivedPriceInCents")
