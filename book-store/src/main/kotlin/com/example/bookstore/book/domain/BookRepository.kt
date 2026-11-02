package com.example.bookstore.book.domain

interface BookRepository {
    fun findAll(): List<Book>

    fun create(book: Book): Book
}
