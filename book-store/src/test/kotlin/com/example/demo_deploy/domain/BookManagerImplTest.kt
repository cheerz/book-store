package com.example.demo_deploy.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class BookManagerImplTest {

    private class FakeBookRepository(private val stored: MutableList<Book> = mutableListOf()) : BookRepository {
        override fun findAll(): List<Book> = stored

        override fun create(book: Book): Book {
            stored.add(book)
            return book
        }
    }

    @Test
    fun `returns the books held by the repository`() {
        val dune = Book("Dune", BookKind.SF)
        val bookManager = BookManagerImpl(FakeBookRepository(mutableListOf(dune)))

        assertEquals(listOf(dune), bookManager.getBooks())
    }

    @Test
    fun `stores a new book in the repository`() {
        val repository = FakeBookRepository()
        val bookManager = BookManagerImpl(repository)

        val created = bookManager.createBook(Book("Shining", BookKind.THRILLER))

        assertEquals(Book("Shining", BookKind.THRILLER), created)
        assertEquals(listOf(created), repository.findAll())
    }
}
