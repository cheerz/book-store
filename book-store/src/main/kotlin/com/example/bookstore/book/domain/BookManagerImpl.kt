package com.example.bookstore.book.domain

import org.springframework.stereotype.Service

@Service
class BookManagerImpl(
    private val bookRepository: BookRepository,
) : BookManager {
    override fun getBooks(): List<Book> = bookRepository.findAll()

    override fun getBook(id: Long): Book = bookRepository.findById(id) ?: throw BookNotFoundException(id)

    override fun createBook(book: Book): Book = bookRepository.create(book)
}
