package com.example.demo_deploy.adapter.outbound.repository

import com.example.demo_deploy.domain.Book
import com.example.demo_deploy.domain.BookKind
import com.example.demo_deploy.domain.BookRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryBookRepository: BookRepository {

    val books: MutableList<Book> = mutableListOf(Book("LSDA : La communauté de l'anneau", BookKind.FANTASY),
        Book("LSDA : Les deux tours", BookKind.FANTASY), Book("LSDA : Le retour du roi", BookKind.FANTASY))

    override fun findAll(): List<Book> {
        return books
    }

    override fun create(book: Book): Book {
        books.add(book)
        return books[books.lastIndex]
    }

}