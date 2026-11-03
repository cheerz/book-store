package com.example.bookstore.purchase.domain

class OutOfStockException(
    val bookId: Long,
    val requested: Int,
    val available: Int,
) : RuntimeException("Only $available item(s) left for book $bookId (requested: $requested)")
