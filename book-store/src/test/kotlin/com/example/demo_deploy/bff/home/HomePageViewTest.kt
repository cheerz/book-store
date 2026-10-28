package com.example.demo_deploy.bff.home

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookKind
import com.example.demo_deploy.catalog.domain.BookManager
import com.example.demo_deploy.catalog.domain.BookSize
import kotlin.test.Test
import kotlin.test.assertEquals

class HomePageViewTest {

    private val books = object : BookManager {
        override fun getBooks() = listOf(
            Book("Bilbo", BookKind.FANTASY, BookSize.SMALL, 1_000),
            Book("Dune", BookKind.SF, BookSize.SMALL, 1_000),
            Book("Hyperion", BookKind.SF, BookSize.SMALL, 1_000),
            Book("Shining", BookKind.THRILLER, BookSize.SMALL, 1_000),
        )
        override fun createBook(book: Book) = book
    }

    @Test
    fun `summarises the catalog for the home page`() {
        val page = HomePageView(books).page()

        assertEquals(4, page.bookCount)
        assertEquals(
            listOf(KindTile("fantasy", 1), KindTile("sf", 2), KindTile("thriller", 1)),
            page.kinds,
        )
        assertEquals(listOf("Shining", "Hyperion", "Dune"), page.latestTitles)
    }
}
