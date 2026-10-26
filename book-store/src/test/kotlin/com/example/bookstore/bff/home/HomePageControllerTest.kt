package com.example.bookstore.bff.home

import com.example.bookstore.catalog.domain.Book
import com.example.bookstore.catalog.domain.BookFormat
import com.example.bookstore.catalog.domain.BookKind
import com.example.bookstore.catalog.domain.BookManager
import kotlin.test.Test
import kotlin.test.assertEquals

class HomePageControllerTest {
    private val books =
        object : BookManager {
            override fun getBooks() =
                listOf(
                    Book("Bilbo", BookKind.FANTASY, BookFormat.E_BOOK, 1_000),
                    Book("Dune", BookKind.SF, BookFormat.E_BOOK, 1_000),
                    Book("Hyperion", BookKind.SF, BookFormat.E_BOOK, 1_000),
                    Book("Shining", BookKind.THRILLER, BookFormat.E_BOOK, 1_000),
                )

            override fun createBook(book: Book) = book
        }

    @Test
    fun `summarises the catalog for the home page`() {
        val page = HomePageController(books).page()

        assertEquals(4, page.bookCount)
        assertEquals(
            listOf(KindTile("fantasy", 1), KindTile("sf", 2), KindTile("thriller", 1)),
            page.kinds,
        )
        assertEquals(listOf("Shining", "Hyperion", "Dune"), page.latestTitles)
    }
}
