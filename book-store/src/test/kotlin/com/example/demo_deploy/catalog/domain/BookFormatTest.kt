package com.example.demo_deploy.catalog.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookFormatTest {

    @Test
    fun `resolves an accepted format whatever its case`() {
        assertEquals(BookFormat.E_BOOK, BookFormat.fromLabel(" E-Book "))
    }

    @Test
    fun `rejects a format outside the accepted ones`() {
        val refused = assertFailsWith<InvalidBookFormatException> {
            BookFormat.fromLabel("huge")
        }

        assertEquals("huge", refused.receivedFormat)
    }
}
