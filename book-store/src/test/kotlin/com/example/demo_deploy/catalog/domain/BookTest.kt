package com.example.demo_deploy.catalog.domain

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
}
