package com.example.bookstore.it.scenario

import com.example.bookstore.it.support.IntegrationTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SmokeIT : IntegrationTest() {
    @Test
    fun `the catalog page is reachable`() {
        assertTrue(pages.catalogPage().books.isNotEmpty())
    }
}
