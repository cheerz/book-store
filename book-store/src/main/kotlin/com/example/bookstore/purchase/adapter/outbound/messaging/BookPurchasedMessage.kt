package com.example.bookstore.purchase.adapter.outbound.messaging

import com.example.bookstore.purchase.domain.BookPurchased
import java.time.Instant
import java.util.UUID

data class BookPurchasedMessage(
    val eventId: UUID,
    val bookId: Long,
    val quantity: Int,
    val occurredAt: Instant,
) {
    companion object {
        fun from(event: BookPurchased): BookPurchasedMessage =
            BookPurchasedMessage(
                eventId = event.eventId,
                bookId = event.bookId,
                quantity = event.quantity,
                occurredAt = event.occurredAt,
            )
    }
}
