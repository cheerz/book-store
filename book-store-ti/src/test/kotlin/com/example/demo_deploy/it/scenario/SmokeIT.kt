package com.example.demo_deploy.it.scenario

import com.example.demo_deploy.it.client.BookPayload
import com.example.demo_deploy.it.support.IntegrationTest
import feign.FeignException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SmokeIT : IntegrationTest() {

    @Test
    fun `the book catalogue is reachable`() {
        assertTrue(bookApi.listBooks().isNotEmpty())
    }

    @Test
    fun `I can add a book to the Catalogue`() {
        val created = bookApi.addBook(BookPayload(uniqueTitle("epee"), "Fantasy"))
        assertEquals("fantasy", created.bookKind)
        assertTrue(bookApi.listBooks().contains(created))
    }

    @Test
    fun `I can't add a romance book to the Catalogue`() {
        val romance = BookPayload("Les feux de l'amour", "Romance")

        val refused = assertFailsWith<FeignException.BadRequest> {
            bookApi.addBook(romance)
        }

        assertTrue(refused.contentUTF8().contains("INVALID_BOOK_KIND"))
        assertFalse(bookApi.listBooks().any { it.title == romance.title })
    }
}
