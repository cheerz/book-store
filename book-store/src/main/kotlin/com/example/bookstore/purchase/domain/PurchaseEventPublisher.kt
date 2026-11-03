package com.example.bookstore.purchase.domain

interface PurchaseEventPublisher {
    fun publish(event: BookPurchased)
}
