package com.example.bookstore.purchase.domain

interface PurchaseManager {
    fun purchase(
        lines: List<PurchaseLine>,
        availableQuantities: Map<Long, Int>,
    ): PurchaseOrder
}
