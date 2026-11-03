package com.example.bookstore.purchase.domain

data class PurchaseLine(
    val bookId: Long,
    val quantity: Int,
)
