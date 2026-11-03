package com.example.bookstore.bff

import com.example.bookstore.bff.catalog.MissingFormatException
import com.example.bookstore.book.domain.Book
import com.example.bookstore.format.domain.Format

fun Map<String, Format>.formatOf(book: Book): Format = this[book.formatCode] ?: throw MissingFormatException(book.formatCode)

// book and format do not know each other: the BFF is the only place holding both prices.
fun totalPriceInCents(
    book: Book,
    format: Format,
): Long = book.priceInCents + format.surcharge.amountInCents
