package com.example.bookstore.bff.catalog

import com.example.bookstore.bff.InvalidBookKindException
import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookKind
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import com.example.bookstore.format.domain.Price
import com.example.bookstore.format.domain.UnknownFormatException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CatalogPageControllerTest {
    private val books =
        object : BookManager {
            val stored =
                mutableListOf(
                    Book("Dune", BookKind.SF, "HARDCOVER", 1_200, id = 1),
                    Book("Shining", BookKind.THRILLER, "E_BOOK", 900, id = 2),
                )

            override fun getBooks() = stored.toList()

            override fun createBook(book: Book) = book.copy(id = stored.size + 1L).also { stored.add(it) }
        }

    private val formats =
        object : FormatManager {
            override fun getFormats() =
                listOf(
                    Format("E_BOOK", "e-book", Price(500)),
                    Format("POCKET", "pocket", Price(750)),
                    Format("HARDCOVER", "hardcover", Price(1_000)),
                )

            override fun formatOf(label: String) =
                getFormats().find { it.label == label } ?: throw UnknownFormatException(label, emptyList())
        }

    private val controller = CatalogPageController(books, formats)

    @Test
    fun `shows each book with its price, its format price and their total`() {
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
        assertFailsWith<UnknownFormatException> {
            controller.addBook(AddBookRequestDto("Dune", "sf", "huge", 500))
        }

        assertEquals(2, controller.page().books.size)
    }

    @Test
    fun `cannot show a stored book whose format is no longer offered`() {
        books.stored.add(Book("Hyperion", BookKind.SF, "LEATHER", 1_500, id = 3))

        val missing = assertFailsWith<MissingFormatException> { controller.page() }

        assertEquals("LEATHER", missing.formatCode)
    }
}
