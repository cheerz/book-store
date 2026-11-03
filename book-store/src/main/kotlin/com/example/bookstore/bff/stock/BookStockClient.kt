package com.example.bookstore.bff.stock

// book-stock owns the stock: book-store keeps no stock domain, the BFF reads and feeds it directly.
interface BookStockClient {
    fun quantitiesOf(bookIds: List<Long>): Map<Long, Int>

    fun addItem(bookId: Long): Int
}
