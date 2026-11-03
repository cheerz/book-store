package com.example.bookstore.it.scenario

import com.example.bookstore.it.client.BookPayload
import com.example.bookstore.it.client.PurchasePayload
import com.example.bookstore.it.support.IntegrationTest
import feign.FeignException
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PurchaseIT : IntegrationTest() {
    @Test
    fun `a purchase lowers the stock once book-stock has consumed it`() {
        val bookId = stockedBook(items = 2)

        pages.purchase(PurchasePayload(listOf(bookId)))

        eventually { assertEquals(1, stockOf(bookId)) }
    }

    // Each event is consumed in milliseconds: a consumer stuck in retries takes seconds per book.
    @Test
    fun `a purchase of several books lowers the stock of each one`() {
        val bookIds = List(3) { stockedBook(items = 1) }

        pages.purchase(PurchasePayload(bookIds))

        eventually(timeout = Duration.ofSeconds(3)) { assertEquals(listOf(0, 0, 0), bookIds.map(::stockOf)) }
    }

    @Test
    fun `I can't buy a book out of stock`() {
        val bookId = stockedBook(items = 0)

        val refused = assertFailsWith<FeignException.UnprocessableEntity> { pages.purchase(PurchasePayload(listOf(bookId))) }

        assertTrue(refused.contentUTF8().contains("\"errorCode\":\"BBE-16\""))
    }

    private fun stockedBook(items: Int): Long {
        val title = uniqueTitle("purchase")
        val bookId =
            pages
                .addBook(BookPayload(title, "sf"))
                .books
                .single { it.title == title }
                .id
        repeat(items) { pages.addStockItem(bookId) }
        return bookId
    }

    private fun stockOf(bookId: Long): Int =
        pages
            .catalogPage()
            .books
            .single { it.id == bookId }
            .stock
}
