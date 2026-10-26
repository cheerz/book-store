package com.example.bookstore.catalog.domain

class InvalidBookTitleException(
    val receivedLength: Int,
) : RuntimeException(
        "A book title must hold 1 to ${ Book.TITLE_MAX_LENGTH } characters (received: $receivedLength)",
    )
