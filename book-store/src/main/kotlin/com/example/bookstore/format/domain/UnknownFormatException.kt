package com.example.bookstore.format.domain

class UnknownFormatException(
    val receivedLabel: String,
    acceptedLabels: List<String>,
) : RuntimeException(
        "Only accepted formats: ${ acceptedLabels.joinToString(", ") } (received: $receivedLabel)",
    )
