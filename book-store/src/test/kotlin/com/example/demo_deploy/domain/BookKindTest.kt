package com.example.demo_deploy.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BookKindTest {

    @Test
    fun `resolves an accepted kind whatever its case`() {
        assertEquals(BookKind.SF, BookKind.fromLabel(" Sf "))
    }

    // Règle métier : seuls fantasy, sf et thriller sont acceptés.
    @Test
    fun `rejects a kind outside the accepted ones`() {
        val refused = assertFailsWith<InvalidBookKindException> {
            BookKind.fromLabel("romance")
        }

        assertEquals("romance", refused.receivedKind)
    }
}
