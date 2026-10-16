package com.example.demo_deploy.catalog.adapter.outbound.repository

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookKind
import com.example.demo_deploy.catalog.domain.BookRepository
import com.example.demo_deploy.catalog.domain.BookSize
import org.springframework.stereotype.Repository

@Repository
class InMemoryBookRepository : BookRepository {

    val books: MutableList<Book> = mutableListOf(
        Book("LSDA : La communauté de l'anneau", BookKind.FANTASY, BookSize.BIG, 1_500),
        Book("LSDA : Les deux tours", BookKind.FANTASY, BookSize.SMALL, 700),
        Book("LSDA : Le retour du roi", BookKind.FANTASY, BookSize.MEDIUM, 1_000),
    )

    override fun findAll(): List<Book> {
        return books
    }

    override fun create(book: Book): Book {
        books.add(book)
        return books[books.lastIndex]
    }
}
