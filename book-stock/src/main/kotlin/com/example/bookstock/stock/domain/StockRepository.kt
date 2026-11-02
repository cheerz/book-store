package com.example.bookstock.stock.domain

interface StockRepository {
    fun findByBookId(bookId: Long): Stock?

    fun save(stock: Stock): Stock
}
