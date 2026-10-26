package com.example.bookstore.bff

import com.example.bookstore.catalog.domain.BookFormat
import com.example.bookstore.catalog.domain.BookKind

val BookKind.label: String
    get() =
        when (this) {
            BookKind.FANTASY -> "fantasy"
            BookKind.SF -> "sf"
            BookKind.THRILLER -> "thriller"
        }

val BookFormat.label: String
    get() =
        when (this) {
            BookFormat.E_BOOK -> "e-book"
            BookFormat.POCKET -> "pocket"
            BookFormat.HARDCOVER -> "hardcover"
        }

fun bookKindOf(label: String): BookKind =
    BookKind.entries.find { it.label == normalize(label) }
        ?: throw InvalidBookKindException(label)

fun bookFormatOf(label: String): BookFormat =
    BookFormat.entries.find { it.label == normalize(label) }
        ?: throw InvalidBookFormatException(label)

private fun normalize(label: String): String = label.trim().lowercase()
