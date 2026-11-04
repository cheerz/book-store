package com.example.bookstore.purchase.domain

import org.springframework.stereotype.Service

@Service
class PurchaseManagerImpl(
    private val purchaseOrders: PurchaseOrderRepository,
    private val purchaseEventPublisher: PurchaseEventPublisher,
) : PurchaseManager {
    // No outbox: the order insert and the publications are separate writes, so a crash or a broker
    // failure between them leaves a recorded order that book-stock never hears about.
    override fun purchase(
        lines: List<PurchaseLine>,
        availableQuantities: Map<Long, Int>,
    ): PurchaseOrder {
        checkQuantityOfEachBook(lines, availableQuantities)
        val order = recordOrder(lines)
        sendBookPurchasedEvents(lines)
        return order
    }

    // The check narrows overselling but cannot close it: two purchases can read the same quantity
    // before book-stock applies either. book-stock then lets the stock go negative.
    private fun checkQuantityOfEachBook(
        lines: List<PurchaseLine>,
        availableQuantities: Map<Long, Int>,
    ) {
        lines.forEach { line ->
            val available = availableQuantities.getOrDefault(line.bookId, 0)
            if (line.quantity > available) throw OutOfStockException(line.bookId, line.quantity, available)
        }
    }

    private fun recordOrder(lines: List<PurchaseLine>): PurchaseOrder = PurchaseOrder.place(lines).also(purchaseOrders::save)

    private fun sendBookPurchasedEvents(lines: List<PurchaseLine>) {
        lines.forEach { purchaseEventPublisher.publish(BookPurchased.of(it)) }
    }
}
