package com.example.bookstock.stock.adapter.outbound.repository

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "stocks")
class StockEntity(
    @Id
    val bookId: Long,
    @Column(nullable = false)
    val quantity: Int,
)
