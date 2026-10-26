package com.example.bookstore.it.scenario

import com.example.bookstore.it.client.BookPayload
import com.example.bookstore.it.support.IntegrationTest
import feign.FeignException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmokeIT : IntegrationTest() {
    @Test
    fun `the catalog page is reachable`() {
        assertTrue(pages.catalogPage().books.isNotEmpty())
    }

    @Test
    fun `I can add a book to the catalog`() {
        val title = uniqueTitle("epee")

        val page = pages.addBook(BookPayload(title, "Fantasy"))

        assertEquals("fantasy", page.books.single { it.title == title }.kind)
    }

    @Test
    fun `I can't add a romance book to the catalog`() {
        val title = uniqueTitle("les-feux-de-l-amour")

        assertRefusedWith("CBE-10", BookPayload(title, "Romance"))
    }

    @Test
    fun `I can't add a book of an unknown format to the catalog`() {
        val title = uniqueTitle("huge")

        assertRefusedWith("CBE-8", BookPayload(title, "sf", "huge"))
    }

    @Test
    fun `I can't add a book with a negative price to the catalog`() {
        val title = uniqueTitle("negative")

        assertRefusedWith("CBE-9", BookPayload(title, "sf", "e-book", -100))
    }

    private fun assertRefusedWith(
        errorCode: String,
        book: BookPayload,
    ) {
        val refused =
            assertFailsWith<FeignException.UnprocessableEntity> {
                pages.addBook(book)
            }

        assertTrue(refused.contentUTF8().contains("\"errorCode\":\"$errorCode\""))
        assertFalse(pages.catalogPage().books.any { it.title == book.title })
    }
}
