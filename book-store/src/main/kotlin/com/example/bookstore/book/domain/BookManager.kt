package com.example.bookstore.book.domain

interface BookManager {
    fun getBooks(): List<Book>

    fun createBook(book: Book): Book
}
