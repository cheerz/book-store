package com.example.demo_deploy.adapter.outbound.repository

import com.example.demo_deploy.domain.Book
import com.example.demo_deploy.domain.BookRepository
import org.springframework.stereotype.Repository

@Repository
class InMemoryBookRepository: BookRepository {

    val books: MutableList<Book> = mutableListOf(Book("LSDA : La communauté de l'anneau"),
        Book("LSDA : Les deux tours"), Book("LSDA : Le retour du roi"))

    override fun findAll(): List<Book> {
        return books
    }

}