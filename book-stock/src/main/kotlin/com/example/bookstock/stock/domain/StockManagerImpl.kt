package com.example.bookstock.stock.domain

import org.springframework.stereotype.Service

@Service
class StockManagerImpl(
    private val stockRepository: StockRepository,
) : StockManager {
    override fun addItem(bookId: Long): Stock = TODO("add one item to the stock of the book")

    override fun removeItem(bookId: Long): Stock = TODO("remove one item from the stock of the book")
}
