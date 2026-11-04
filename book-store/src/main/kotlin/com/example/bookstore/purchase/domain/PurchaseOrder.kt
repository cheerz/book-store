package com.example.bookstore.purchase.domain

import java.time.Instant
import java.util.UUID

data class PurchaseOrder(
    val id: UUID,
    val lines: List<PurchaseLine>,
    val placedAt: Instant,
) {
    companion object {
        fun place(lines: List<PurchaseLine>): PurchaseOrder = PurchaseOrder(UUID.randomUUID(), lines, Instant.now())
    }
}
