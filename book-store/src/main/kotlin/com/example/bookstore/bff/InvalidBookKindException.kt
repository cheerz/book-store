package com.example.bookstore.bff

import com.example.bookstore.book.domain.BookKind

class InvalidBookKindException(
    val receivedKind: String,
) : RuntimeException(
        "Only accepted kinds: ${ BookKind.entries.joinToString(", ") { it.label } } (received: $receivedKind)",
    )
