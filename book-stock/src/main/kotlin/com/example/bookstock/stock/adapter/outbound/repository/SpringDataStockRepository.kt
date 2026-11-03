package com.example.bookstock.stock.adapter.outbound.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock

interface SpringDataStockRepository : JpaRepository<StockEntity, Long> {
    // Two concurrent movements on the same book would otherwise both read N and both write N ± 1.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findLockedByBookId(bookId: Long): StockEntity?
}
