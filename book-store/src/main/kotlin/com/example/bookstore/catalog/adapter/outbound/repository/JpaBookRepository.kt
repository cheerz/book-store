package com.example.bookstore.catalog.adapter.outbound.repository

import com.example.bookstore.catalog.domain.Book
import com.example.bookstore.catalog.domain.BookRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class JpaBookRepository(
    private val books: SpringDataBookRepository,
) : BookRepository {
    override fun findAll(): List<Book> = books.findAll(Sort.by(BookEntity::id.name)).map { it.toBook() }

    override fun create(book: Book): Book = books.save(BookEntity.from(book)).toBook()
}
