package com.example.demo_deploy.catalog.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookSizeTest {

    @Test
    fun `resolves an accepted size whatever its case`() {
        assertEquals(BookSize.MEDIUM, BookSize.fromLabel(" Medium "))
    }

    @Test
    fun `rejects a size outside the accepted ones`() {
        val refused = assertFailsWith<InvalidBookSizeException> {
            BookSize.fromLabel("huge")
        }

        assertEquals("huge", refused.receivedSize)
    }
}
