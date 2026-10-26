package com.example.bookstore.catalog.domain

class InvalidBookFormatException(
    val receivedFormat: String,
) : RuntimeException(
        "Only accepted formats: ${ BookFormat.entries.joinToString(", ") { it.label } } (received: $receivedFormat)",
    )
