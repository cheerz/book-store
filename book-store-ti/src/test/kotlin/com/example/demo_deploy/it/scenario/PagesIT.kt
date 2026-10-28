package com.example.demo_deploy.it.scenario

import com.example.demo_deploy.it.client.BookPayload
import com.example.demo_deploy.it.support.IntegrationTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PagesIT : IntegrationTest() {

    @Test
    fun `the catalog page shows the price breakdown of each book`() {
        val title = uniqueTitle("dune")

        val item = pages.addBook(BookPayload(title, "sf", "big", 1_200)).books.single { it.title == title }

        assertEquals("sf", item.kind)
        assertEquals("big", item.size)
        assertEquals(1_200L, item.bookPriceInCents)
        assertEquals(500L, item.sizePriceInCents)
        assertEquals(1_700L, item.totalPriceInCents)
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
