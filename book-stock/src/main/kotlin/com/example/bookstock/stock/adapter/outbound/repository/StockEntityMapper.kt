package com.example.bookstock.stock.adapter.outbound.repository

import com.example.bookstock.stock.domain.Stock

object StockEntityMapper {
    fun toDomain(entity: StockEntity): Stock = Stock(bookId = entity.bookId, quantity = entity.quantity)

    fun toEntity(stock: Stock): StockEntity = StockEntity(bookId = stock.bookId, quantity = stock.quantity)
}
