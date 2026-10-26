package com.example.bookstore.bff

import com.example.bookstore.catalog.domain.BookFormat

class InvalidBookFormatException(
    val receivedFormat: String,
) : RuntimeException(
        "Only accepted formats: ${ BookFormat.entries.joinToString(", ") { it.label } } (received: $receivedFormat)",
    )
