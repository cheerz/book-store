package com.example.bookstore.purchase.domain

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class PurchaseManagerImpl(
    private val purchaseEventPublisher: PurchaseEventPublisher,
) : PurchaseManager {
    // The check narrows overselling but cannot close it: two purchases can read the same quantity
    // before book-stock applies either. book-stock then lets the stock go negative.
    override fun purchase(
        lines: List<PurchaseLine>,
        availableQuantities: Map<Long, Int>,
    ): List<BookPurchased> {
        val shortLine = lines.firstOrNull { it.quantity > availableQuantities.getOrDefault(it.bookId, 0) }
        if (shortLine != null) {
            throw OutOfStockException(shortLine.bookId, shortLine.quantity, availableQuantities.getOrDefault(shortLine.bookId, 0))
        }

        return lines.map { line ->
            BookPurchased(UUID.randomUUID(), line.bookId, line.quantity, Instant.now())
                .also(purchaseEventPublisher::publish)
        }
    }
}
