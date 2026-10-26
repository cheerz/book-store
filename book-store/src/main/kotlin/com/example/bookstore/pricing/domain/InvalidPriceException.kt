package com.example.bookstore.pricing.domain

class InvalidPriceException(
    val amountInCents: Long,
) : RuntimeException("A price cannot be negative: $amountInCents")
