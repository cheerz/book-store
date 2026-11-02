package com.example.bookstock.stock.domain

interface StockManager {
    fun addItem(bookId: Long): Stock

    fun removeItem(bookId: Long): Stock
}
