package com.example.bookstore.bff

import com.example.bookstore.catalog.domain.BookFormat
import com.example.bookstore.catalog.domain.BookKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookLabelsTest {
    @Test
    fun `resolves an accepted kind whatever its case`() {
        assertEquals(BookKind.SF, bookKindOf(" Sf "))
    }

    @Test
    fun `rejects a kind outside the accepted ones`() {
        val refused =
            assertFailsWith<InvalidBookKindException> {
                bookKindOf("romance")
            }

        assertEquals("romance", refused.receivedKind)
    }

    @Test
    fun `resolves an accepted format whatever its case`() {
        assertEquals(BookFormat.E_BOOK, bookFormatOf(" E-Book "))
    }

    @Test
    fun `rejects a format outside the accepted ones`() {
        val refused =
            assertFailsWith<InvalidBookFormatException> {
                bookFormatOf("huge")
            }

        assertEquals("huge", refused.receivedFormat)
    }
}
