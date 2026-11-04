package com.example.bookstore.purchase.adapter.outbound.repository

import com.example.bookstore.purchase.domain.PurchaseLine
import com.example.bookstore.purchase.domain.PurchaseOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import java.time.temporal.ChronoUnit
import kotlin.test.Test
import kotlin.test.assertEquals

@DataJpaTest
@Import(JpaPurchaseOrderRepository::class)
class JpaPurchaseOrderRepositoryTest
    @Autowired
    constructor(
        private val repository: JpaPurchaseOrderRepository,
        private val stored: SpringDataPurchaseOrderRepository,
    ) {
        @Test
        fun `records an order with its lines`() {
            val placed = PurchaseOrder.place(listOf(PurchaseLine(1, 2), PurchaseLine(2, 1)))
            val order = placed.copy(placedAt = placed.placedAt.truncatedTo(ChronoUnit.MILLIS))

            repository.save(order)

            assertEquals(order, stored.findByIdOrNull(order.id)?.toPurchaseOrder())
        }
    }
