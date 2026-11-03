package com.example.bookstock.stock.adapter.outbound.repository

import com.example.bookstock.stock.domain.ProcessedEventRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
class JpaProcessedEventRepository(
    private val processedEvents: SpringDataProcessedEventRepository,
) : ProcessedEventRepository {
    override fun isProcessed(eventId: UUID): Boolean = processedEvents.existsById(eventId)

    override fun markProcessed(eventId: UUID) {
        processedEvents.save(ProcessedEventEntity(eventId = eventId, processedAt = Instant.now()))
    }
}
