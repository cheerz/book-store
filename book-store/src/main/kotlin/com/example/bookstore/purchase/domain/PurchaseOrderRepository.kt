package com.example.bookstore.purchase.domain

interface PurchaseOrderRepository {
    fun save(order: PurchaseOrder)
}
