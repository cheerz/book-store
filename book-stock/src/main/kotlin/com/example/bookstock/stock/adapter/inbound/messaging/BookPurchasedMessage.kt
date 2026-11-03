package com.example.bookstock.stock.adapter.inbound.messaging

import java.time.Instant
import java.util.UUID

data class BookPurchasedMessage(
    val eventId: UUID,
    val bookId: Long,
    val quantity: Int,
    val occurredAt: Instant,
)
