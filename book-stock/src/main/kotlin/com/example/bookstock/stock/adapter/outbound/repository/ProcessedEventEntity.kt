package com.example.bookstock.stock.adapter.outbound.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "processed_events")
class ProcessedEventEntity(
    @Id
    val eventId: UUID,
    @Column(nullable = false)
    val processedAt: Instant,
)
