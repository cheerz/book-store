package com.example.bookstock.stock.adapter.inbound.controller

import com.example.bookstock.stock.domain.Stock

data class StockDto(
    val bookId: Long,
    val quantity: Int,
) {
    companion object {
        fun from(stock: Stock): StockDto = StockDto(bookId = stock.bookId, quantity = stock.quantity)
    }
}
