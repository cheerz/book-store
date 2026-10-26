package com.example.demo_deploy.bff.catalog

import com.example.demo_deploy.catalog.domain.Book
import com.example.demo_deploy.catalog.domain.BookFormat
import com.example.demo_deploy.catalog.domain.BookKind
import com.example.demo_deploy.catalog.domain.BookManager
import com.example.demo_deploy.catalog.domain.InvalidBookFormatException
import com.example.demo_deploy.catalog.domain.InvalidBookKindException
import com.example.demo_deploy.pricing.domain.Price
import com.example.demo_deploy.pricing.domain.PriceManager
import com.example.demo_deploy.pricing.domain.PriceQuote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CatalogPageViewTest {
    private val books =
        object : BookManager {
            val stored =
                mutableListOf(
                    Book("Dune", BookKind.SF, BookFormat.HARDCOVER, 1_200),
                    Book("Shining", BookKind.THRILLER, BookFormat.E_BOOK, 900),
                )

            override fun getBooks() = stored.toList()

            override fun createBook(book: Book) = book.also { stored.add(it) }
        }

    private val prices =
        object : PriceManager {
            override fun quote(
                basePriceInCents: Long,
                productType: String,
            ): PriceQuote {
                val surcharge =
                    when (productType) {
                        "HARDCOVER" -> Price(1_000)
                        "POCKET" -> Price(750)
                        "E_BOOK" -> Price(500)
                        else -> error("unexpected $productType")
                    }
                return PriceQuote(Price(basePriceInCents), surcharge, Price(basePriceInCents) + surcharge)
            }
        }

    private val view = CatalogPageView(books, prices)

    @Test
    fun `shows each book with the price breakdown computed by pricing`() {
        assertEquals(
            listOf(
                CatalogPageItem("Dune", "sf", "hardcover", 1_200, 1_000, 2_200),
                CatalogPageItem("Shining", "thriller", "e-book", 900, 500, 1_400),
            ),
            view.page().books,
        )
    }

    @Test
    fun `offers each format with its price`() {
        assertEquals(
            listOf(FormatOption("e-book", 500), FormatOption("pocket", 750), FormatOption("hardcover", 1_000)),
            view.page().formats,
        )
    }

    @Test
    fun `adds a book and returns the refreshed page`() {
        val page = view.addBook(AddBookRequestDto("Bilbo", " Fantasy ", "pocket", 1_250))

        assertEquals(CatalogPageItem("Bilbo", "fantasy", "pocket", 1_250, 750, 2_000), page.books.last())
    }

    @Test
    fun `refuses a book of an unknown kind`() {
        assertFailsWith<InvalidBookKindException> {
            view.addBook(AddBookRequestDto("Les feux de l'amour", "romance", "e-book", 500))
        }

        assertEquals(2, view.page().books.size)
    }

    @Test
    fun `refuses a book of an unknown format`() {
        assertFailsWith<InvalidBookFormatException> {
            view.addBook(AddBookRequestDto("Dune", "sf", "huge", 500))
        }

        assertEquals(2, view.page().books.size)
    }
}
