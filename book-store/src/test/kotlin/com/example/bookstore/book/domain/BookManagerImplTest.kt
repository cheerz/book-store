package com.example.bookstore.book.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookManagerImplTest {
    private class FakeBookRepository(
        private val stored: MutableList<Book> = mutableListOf(),
    ) : BookRepository {
        override fun findAll(): List<Book> = stored

        override fun findById(id: Long): Book? = stored.find { it.id == id }

        override fun create(book: Book): Book {
            stored.add(book)
            return book
        }
    }

    @Test
    fun `returns the books held by the repository`() {
        val dune = Book("Dune", BookKind.SF, "HARDCOVER", 1_200)
        val bookManager = BookManagerImpl(FakeBookRepository(mutableListOf(dune)))

        assertEquals(listOf(dune), bookManager.getBooks())
    }

    @Test
    fun `stores a new book in the repository`() {
        val repository = FakeBookRepository()
        val bookManager = BookManagerImpl(repository)

        val created = bookManager.createBook(Book("Shining", BookKind.THRILLER, "E_BOOK", 900))

        assertEquals(Book("Shining", BookKind.THRILLER, "E_BOOK", 900), created)
        assertEquals(listOf(created), repository.findAll())
    }

    @Test
    fun `gives a stored book by its id`() {
        val dune = Book("Dune", BookKind.SF, "HARDCOVER", 1_200, id = 1)
        val bookManager = BookManagerImpl(FakeBookRepository(mutableListOf(dune)))

        assertEquals(dune, bookManager.getBook(1))
    }

    @Test
    fun `refuses to give a book that does not exist`() {
        val missing = assertFailsWith<BookNotFoundException> { BookManagerImpl(FakeBookRepository()).getBook(1) }

        assertEquals(1, missing.bookId)
    }
}
