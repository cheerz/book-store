package com.example.demo_deploy.catalog.domain

import org.springframework.stereotype.Service

@Service
class BookManagerImpl(private val bookRepository: BookRepository)
    : BookManager {

    override fun getBooks(): List<Book> {
        return bookRepository.findAll()
    }

    override fun createBook(book: Book): Book {
        return bookRepository.create(book)
    }

}