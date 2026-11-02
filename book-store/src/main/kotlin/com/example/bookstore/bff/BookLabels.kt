package com.example.bookstore.bff

import com.example.bookstore.book.domain.BookKind

val BookKind.label: String
    get() =
        when (this) {
            BookKind.FANTASY -> "fantasy"
            BookKind.SF -> "sf"
            BookKind.THRILLER -> "thriller"
        }

fun bookKindOf(label: String): BookKind =
    BookKind.entries.find { it.label == normalize(label) }
        ?: throw InvalidBookKindException(label)

private fun normalize(label: String): String = label.trim().lowercase()
