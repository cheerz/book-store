package com.example.bookstore.it.scenario

import com.example.bookstore.it.client.BookPayload
import com.example.bookstore.it.support.IntegrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PagesIT : IntegrationTest() {
    @Test
    fun `the catalog page shows the price breakdown of each book`() {
        val title = uniqueTitle("dune")

        val item = pages.addBook(BookPayload(title, "sf", "hardcover", 1_200)).books.single { it.title == title }

        assertEquals("sf", item.kind)
        assertEquals("hardcover", item.format)
        assertEquals(1_200L, item.bookPriceInCents)
        assertEquals(1_000L, item.formatPriceInCents)
        assertEquals(2_200L, item.totalPriceInCents)
    }

    @Test
    fun `a book added on the catalog page appears on the home page`() {
        val title = uniqueTitle("shining")
        val catalog = pages.addBook(BookPayload(title, "thriller"))

        val home = pages.homePage()

        assertEquals(title, home.latestTitles.first())
        assertEquals(catalog.books.size, home.bookCount)
    }
}
