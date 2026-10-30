package com.example.bookstore.bff.catalog

class MissingFormatException(
    val formatCode: String,
) : RuntimeException("No format configured for code: $formatCode")
