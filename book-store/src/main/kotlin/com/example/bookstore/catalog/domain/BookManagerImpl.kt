package com.example.bookstore.catalog.domain

import org.springframework.stereotype.Service

@Service
class BookManagerImpl(
    private val bookRepository: BookRepository,
) : BookManager {
    override fun getBooks(): List<Book> = bookRepository.findAll()

    override fun createBook(book: Book): Book = bookRepository.create(book)
}
