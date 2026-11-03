package com.example.bookstore.book.domain

class BookNotFoundException(
    val bookId: Long,
) : RuntimeException("No book with id: $bookId")
