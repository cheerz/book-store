package com.example.bookstore.purchase.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PurchaseManagerImplTest {
    private class FakePurchaseEventPublisher : PurchaseEventPublisher {
        val published = mutableListOf<BookPurchased>()

        override fun publish(event: BookPurchased) {
            published.add(event)
        }
    }

    private val publisher = FakePurchaseEventPublisher()
    private val purchaseManager = PurchaseManagerImpl(publisher)
    private val available = mapOf(1L to 2, 2L to 1)

    @Test
    fun `publishes one purchase per book bought`() {
        val purchases = purchaseManager.purchase(listOf(PurchaseLine(1, 2), PurchaseLine(2, 1)), available)

        assertEquals(listOf(PurchaseLine(1, 2), PurchaseLine(2, 1)), purchases.map { PurchaseLine(it.bookId, it.quantity) })
        assertEquals(purchases, publisher.published)
    }

    @Test
    fun `refuses the whole purchase when one book is beyond its stock`() {
        val refused =
            assertFailsWith<OutOfStockException> {
                purchaseManager.purchase(listOf(PurchaseLine(1, 1), PurchaseLine(2, 2)), available)
            }

        assertEquals(2, refused.bookId)
        assertEquals(emptyList(), publisher.published)
    }
}
