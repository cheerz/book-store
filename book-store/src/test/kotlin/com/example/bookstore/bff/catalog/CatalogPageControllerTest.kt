package com.example.bookstore.bff.catalog

import com.example.bookstore.catalog.domain.Book
import com.example.bookstore.catalog.domain.BookFormat
import com.example.bookstore.catalog.domain.BookKind
import com.example.bookstore.catalog.domain.BookManager
import com.example.bookstore.catalog.domain.InvalidBookFormatException
import com.example.bookstore.catalog.domain.InvalidBookKindException
import com.example.bookstore.pricing.domain.Price
import com.example.bookstore.pricing.domain.PriceManager
import com.example.bookstore.pricing.domain.PriceQuote
import com.example.bookstore.pricing.domain.ProductType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CatalogPageControllerTest {
    private val books =
        object : BookManager {
            val stored =
                mutableListOf(
                    Book("Dune", BookKind.SF, BookFormat.HARDCOVER, 1_200, id = 1),
                    Book("Shining", BookKind.THRILLER, BookFormat.E_BOOK, 900, id = 2),
                )

            override fun getBooks() = stored.toList()

            override fun createBook(book: Book) = book.copy(id = stored.size + 1L).also { stored.add(it) }
        }

    private val prices =
        object : PriceManager {
            override fun quote(
                basePriceInCents: Long,
                productType: ProductType,
            ): PriceQuote {
                val surcharge = surchargeFor(productType)
                return PriceQuote(Price(basePriceInCents), surcharge, Price(basePriceInCents) + surcharge)
            }

            override fun surchargeFor(productType: ProductType): Price =
                when (productType) {
                    ProductType.HARDCOVER -> Price(1_000)
                    ProductType.POCKET -> Price(750)
                    ProductType.E_BOOK -> Price(500)
                }
        }

    private val controller = CatalogPageController(books, prices)

    @Test
    fun `shows each book with the price breakdown computed by pricing`() {
        assertEquals(
            listOf(
                CatalogPageItem(1, "Dune", "sf", "hardcover", 1_200, 1_000, 2_200),
                CatalogPageItem(2, "Shining", "thriller", "e-book", 900, 500, 1_400),
            ),
            controller.page().books,
        )
    }

    @Test
    fun `offers each format with its price`() {
        assertEquals(
            listOf(FormatOption("e-book", 500), FormatOption("pocket", 750), FormatOption("hardcover", 1_000)),
            controller.page().formats,
        )
    }

    @Test
    fun `adds a book and returns the refreshed page`() {
        val page = controller.addBook(AddBookRequestDto("Bilbo", " Fantasy ", "pocket", 1_250))

        assertEquals(CatalogPageItem(3, "Bilbo", "fantasy", "pocket", 1_250, 750, 2_000), page.books.last())
    }

    @Test
    fun `refuses a book of an unknown kind`() {
        assertFailsWith<InvalidBookKindException> {
            controller.addBook(AddBookRequestDto("Les feux de l'amour", "romance", "e-book", 500))
        }

        assertEquals(2, controller.page().books.size)
    }

    @Test
    fun `refuses a book of an unknown format`() {
        assertFailsWith<InvalidBookFormatException> {
            controller.addBook(AddBookRequestDto("Dune", "sf", "huge", 500))
        }

        assertEquals(2, controller.page().books.size)
    }
}
