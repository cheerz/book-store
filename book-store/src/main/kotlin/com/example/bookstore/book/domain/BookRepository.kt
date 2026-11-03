package com.example.bookstore.book.domain

interface BookRepository {
    fun findAll(): List<Book>

    fun findById(id: Long): Book?

    fun create(book: Book): Book
}
