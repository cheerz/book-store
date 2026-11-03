package com.example.bookstock.stock.adapter.outbound.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataProcessedEventRepository : JpaRepository<ProcessedEventEntity, UUID>
