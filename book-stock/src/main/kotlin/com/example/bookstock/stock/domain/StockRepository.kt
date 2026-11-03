package com.example.bookstock.stock.domain

interface StockRepository {
    fun findByBookId(bookId: Long): Stock?

    fun findByBookIds(bookIds: List<Long>): List<Stock>

    fun findByBookIdForUpdate(bookId: Long): Stock?

    fun save(stock: Stock): Stock
}
