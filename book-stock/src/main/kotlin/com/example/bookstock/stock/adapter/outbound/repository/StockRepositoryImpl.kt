package com.example.bookstock.stock.adapter.outbound.repository

import com.example.bookstock.stock.domain.Stock
import com.example.bookstock.stock.domain.StockRepository
import org.springframework.stereotype.Repository

@Repository
class StockRepositoryImpl : StockRepository {
    override fun findByBookId(bookId: Long): Stock? = TODO("read the stock of the book")

    override fun save(stock: Stock): Stock = TODO("persist the stock")
}
