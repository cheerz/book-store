package com.example.bookstore.catalog.domain

interface BookManager {
    fun getBooks(): List<Book>

    fun createBook(book: Book): Book
}
