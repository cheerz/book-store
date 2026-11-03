package com.example.bookstore.purchase.domain

import java.time.Instant
import java.util.UUID

data class BookPurchased(
    val eventId: UUID,
    val bookId: Long,
    val quantity: Int,
    val occurredAt: Instant,
)
