package com.example.bookstock.stock.adapter.outbound.repository

import com.example.bookstock.stock.domain.Stock
import com.example.bookstock.stock.domain.StockRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class JpaStockRepository(
    private val stocks: SpringDataStockRepository,
) : StockRepository {
    override fun findByBookId(bookId: Long): Stock? = stocks.findByIdOrNull(bookId)?.let(StockEntityMapper::toDomain)

    override fun findByBookIds(bookIds: List<Long>): List<Stock> = stocks.findAllById(bookIds).map(StockEntityMapper::toDomain)

    override fun findByBookIdForUpdate(bookId: Long): Stock? = stocks.findLockedByBookId(bookId)?.let(StockEntityMapper::toDomain)

    override fun save(stock: Stock): Stock = StockEntityMapper.toDomain(stocks.save(StockEntityMapper.toEntity(stock)))
}
