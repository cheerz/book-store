package com.example.bookstock.stock.domain

import java.util.UUID

interface StockManager {
    fun getStock(bookId: Long): Stock

    fun getStocks(bookIds: List<Long>): List<Stock>

    fun addItem(bookId: Long): Stock

    fun removeItem(
        eventId: UUID,
        bookId: Long,
        quantity: Int,
    ): Stock
}
