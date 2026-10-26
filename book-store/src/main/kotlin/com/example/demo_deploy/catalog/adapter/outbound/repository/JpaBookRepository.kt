package com.example.demo_deploy.catalog.adapter.outbound.repository

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
class JpaBookRepository(
    private val books: SpringDataBookRepository,
) : BookRepository {
    // Tri par id = ordre d'ajout : la page d'accueil en tire ses « derniers ajouts ».
    override fun findAll(): List<Book> = books.findAll(Sort.by(BookEntity::id.name)).map { it.toBook() }

    override fun create(book: Book): Book = books.save(BookEntity.from(book)).toBook()
}
