package com.example.bookstore.book.domain

interface BookManager {
    fun getBooks(): List<Book>

    fun getBook(id: Long): Book

    fun createBook(book: Book): Book
}
