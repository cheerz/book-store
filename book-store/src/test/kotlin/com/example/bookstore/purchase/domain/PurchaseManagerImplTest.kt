package com.example.bookstore.purchase.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PurchaseManagerImplTest {
    private class FakePurchaseOrderRepository : PurchaseOrderRepository {
        val saved = mutableListOf<PurchaseOrder>()

        override fun save(order: PurchaseOrder) {
            saved.add(order)
        }
    }

    private class FakePurchaseEventPublisher : PurchaseEventPublisher {
        val published = mutableListOf<BookPurchased>()

        override fun publish(event: BookPurchased) {
            published.add(event)
        }
    }

    private val orders = FakePurchaseOrderRepository()
    private val publisher = FakePurchaseEventPublisher()
    private val purchaseManager = PurchaseManagerImpl(orders, publisher)
    private val available = mapOf(1L to 2, 2L to 1)

    @Test
    fun `records the order and publishes one purchase per book bought`() {
        val lines = listOf(PurchaseLine(1, 2), PurchaseLine(2, 1))

        val order = purchaseManager.purchase(lines, available)

        assertEquals(listOf(order), orders.saved)
        assertEquals(lines, order.lines)
        assertEquals(lines, publisher.published.map { PurchaseLine(it.bookId, it.quantity) })
    }

    @Test
    fun `refuses the whole purchase when one book is beyond its stock`() {
        val refused =
            assertFailsWith<OutOfStockException> {
                purchaseManager.purchase(listOf(PurchaseLine(1, 1), PurchaseLine(2, 2)), available)
            }

        assertEquals(2, refused.bookId)
        assertEquals(emptyList(), orders.saved)
        assertEquals(emptyList(), publisher.published)
    }
}
