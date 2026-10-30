package com.example.bookstore.book.domain

data class Book(
    val title: String,
    val kind: BookKind,
    val formatCode: String,
    val priceInCents: Long,
    val id: Long? = null,
) {
    init {
        if (title.isBlank() || title.length > TITLE_MAX_LENGTH) throw InvalidBookTitleException(title.length)
        if (priceInCents < 0) throw InvalidBookPriceException(priceInCents)
    }

    companion object {
        const val TITLE_MAX_LENGTH = 255
    }
}
