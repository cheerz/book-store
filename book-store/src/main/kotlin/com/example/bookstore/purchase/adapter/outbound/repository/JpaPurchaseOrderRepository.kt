package com.example.bookstore.purchase.adapter.outbound.repository

import com.example.bookstore.purchase.domain.PurchaseOrder
import com.example.bookstore.purchase.domain.PurchaseOrderRepository
import org.springframework.stereotype.Repository

@Repository
class JpaPurchaseOrderRepository(
    private val purchaseOrders: SpringDataPurchaseOrderRepository,
) : PurchaseOrderRepository {
    override fun save(order: PurchaseOrder) {
        purchaseOrders.save(PurchaseOrderEntity.from(order))
    }
}
