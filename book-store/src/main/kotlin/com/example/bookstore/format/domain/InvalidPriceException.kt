package com.example.bookstore.format.domain

class InvalidPriceException(
    val amountInCents: Long,
) : RuntimeException("A price cannot be negative: $amountInCents")
