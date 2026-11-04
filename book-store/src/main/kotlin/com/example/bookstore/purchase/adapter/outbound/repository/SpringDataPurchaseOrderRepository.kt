package com.example.bookstore.purchase.adapter.outbound.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataPurchaseOrderRepository : JpaRepository<PurchaseOrderEntity, UUID>
