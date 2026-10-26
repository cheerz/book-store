package com.example.bookstore.catalog.domain

interface BookRepository {
    fun findAll(): List<Book>

    fun create(book: Book): Book
}
