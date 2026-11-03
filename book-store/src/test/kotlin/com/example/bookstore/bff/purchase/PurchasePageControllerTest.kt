package com.example.bookstore.bff.purchase

import com.example.bookstore.bff.stock.BookStockClient
import com.example.bookstore.book.domain.Book
import com.example.bookstore.book.domain.BookKind
import com.example.bookstore.book.domain.BookManager
import com.example.bookstore.book.domain.BookNotFoundException
import com.example.bookstore.format.domain.Format
import com.example.bookstore.format.domain.FormatManager
import com.example.bookstore.format.domain.Price
import com.example.bookstore.format.domain.UnknownFormatException
import com.example.bookstore.purchase.domain.BookPurchased
import com.example.bookstore.purchase.domain.PurchaseLine
import com.example.bookstore.purchase.domain.PurchaseManager
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PurchasePageControllerTest {
    private val books =
        object : BookManager {
            val stored =
                listOf(
                    Book("Dune", BookKind.SF, "HARDCOVER", 1_200, id = 1),
                    Book("Shining", BookKind.THRILLER, "E_BOOK", 900, id = 2),
                )

            override fun getBooks() = stored

            override fun getBook(id: Long) = stored.find { it.id == id } ?: throw BookNotFoundException(id)

            override fun createBook(book: Book) = book
        }

    private val formats =
        object : FormatManager {
            override fun getFormats() =
                listOf(
                    Format("E_BOOK", "e-book", Price(500)),
                    Format("HARDCOVER", "hardcover", Price(1_000)),
                )

            override fun formatOf(label: String) =
                getFormats().find { it.label == label } ?: throw UnknownFormatException(label, emptyList())
        }

    private val purchases =
        object : PurchaseManager {
            val purchased = mutableListOf<PurchaseLine>()

            override fun purchase(
                lines: List<PurchaseLine>,
                availableQuantities: Map<Long, Int>,
            ): List<BookPurchased> {
                purchased.addAll(lines)
                return lines.map { BookPurchased(UUID.randomUUID(), it.bookId, it.quantity, Instant.now()) }
            }
        }

    private val stocks =
        object : BookStockClient {
            override fun quantitiesOf(bookIds: List<Long>) = mapOf(1L to 3)

            override fun addItem(bookId: Long) = 1
        }

    private val controller = PurchasePageController(books, formats, purchases, stocks)

    @Test
    fun `shows every book with its total price and its stock`() {
        assertEquals(
            listOf(
                PurchasePageItem(1, "Dune", "sf", "hardcover", 2_200, stock = 3),
                PurchasePageItem(2, "Shining", "thriller", "e-book", 1_400, stock = 0),
            ),
            controller.page().books,
        )
    }

    @Test
    fun `buys one item of each selected book`() {
        controller.purchase(PurchaseRequestDto(bookIds = listOf(1, 2)))

        assertEquals(listOf(PurchaseLine(1, 1), PurchaseLine(2, 1)), purchases.purchased)
    }

    @Test
    fun `refuses to buy a book outside the catalog`() {
        assertFailsWith<BookNotFoundException> { controller.purchase(PurchaseRequestDto(bookIds = listOf(1, 99))) }

        assertEquals(emptyList(), purchases.purchased)
    }
}
