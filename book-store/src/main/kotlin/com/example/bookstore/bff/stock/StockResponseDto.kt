package com.example.bookstore.bff.stock

data class StockResponseDto(
    val bookId: Long,
    val quantity: Int,
)
