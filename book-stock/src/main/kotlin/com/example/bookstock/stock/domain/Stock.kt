package com.example.bookstock.stock.domain

data class Stock(
    val bookId: Long,
    val quantity: Int,
) {
    val isNegative: Boolean get() = quantity < 0

    fun withOneMoreItem(): Stock = copy(quantity = quantity + 1)

    fun withItemsRemoved(removed: Int): Stock = copy(quantity = quantity - removed)

    companion object {
        fun empty(bookId: Long): Stock = Stock(bookId = bookId, quantity = 0)
    }
}
