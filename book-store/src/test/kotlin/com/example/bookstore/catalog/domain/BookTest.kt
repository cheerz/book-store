package com.example.bookstore.catalog.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookTest {
    @Test
    fun `rejects a negative price`() {
        val refused =
            assertFailsWith<InvalidBookPriceException> {
                Book("Dune", BookKind.SF, BookFormat.E_BOOK, -1)
            }

        assertEquals(-1, refused.receivedPriceInCents)
    }

    @Test
    fun `rejects a blank title`() {
        assertFailsWith<InvalidBookTitleException> {
            Book("  ", BookKind.SF, BookFormat.E_BOOK, 1_000)
        }
    }

    @Test
    fun `rejects a title longer than the catalog accepts`() {
        val refused =
            assertFailsWith<InvalidBookTitleException> {
                Book("a".repeat(Book.TITLE_MAX_LENGTH + 1), BookKind.SF, BookFormat.E_BOOK, 1_000)
            }

        assertEquals(Book.TITLE_MAX_LENGTH + 1, refused.receivedLength)
    }
}
