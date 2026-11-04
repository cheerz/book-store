package com.example.bookstore.purchase.adapter.outbound.repository

import com.example.bookstore.purchase.domain.PurchaseLine
import com.example.bookstore.purchase.domain.PurchaseOrder
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OrderColumn
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "purchase_orders")
class PurchaseOrderEntity(
    @Id
    val id: UUID,
    @Column(nullable = false)
    val placedAt: Instant,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "purchase_order_lines", joinColumns = [JoinColumn(name = "purchase_order_id")])
    @OrderColumn(name = "position")
    val lines: List<PurchaseOrderLineEmbeddable>,
) {
    fun toPurchaseOrder(): PurchaseOrder =
        PurchaseOrder(
            id = id,
            lines = lines.map { PurchaseLine(it.bookId, it.quantity) },
            placedAt = placedAt,
        )

    companion object {
        fun from(order: PurchaseOrder): PurchaseOrderEntity =
            PurchaseOrderEntity(
                id = order.id,
                placedAt = order.placedAt,
                lines = order.lines.map { PurchaseOrderLineEmbeddable(it.bookId, it.quantity) },
            )
    }
}

@Embeddable
class PurchaseOrderLineEmbeddable(
    @Column(nullable = false)
    val bookId: Long,
    @Column(nullable = false)
    val quantity: Int,
)
