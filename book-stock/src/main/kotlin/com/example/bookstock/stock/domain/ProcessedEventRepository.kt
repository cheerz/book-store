package com.example.bookstock.stock.domain

import java.util.UUID

interface ProcessedEventRepository {
    fun isProcessed(eventId: UUID): Boolean

    fun markProcessed(eventId: UUID)
}
