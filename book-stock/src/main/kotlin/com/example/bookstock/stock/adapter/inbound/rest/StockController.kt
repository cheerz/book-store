package com.example.bookstock.stock.adapter.inbound.rest

import com.example.bookstock.stock.domain.Stock
import com.example.bookstock.stock.domain.StockManager
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stock/{bookId}")
class StockController(
    private val stockManager: StockManager,
) {
    @PostMapping("/add-item")
    fun addItem(
        @PathVariable bookId: Long,
    ): Stock = stockManager.addItem(bookId)

    @PostMapping("/remove-item")
    fun removeItem(
        @PathVariable bookId: Long,
    ): Stock = stockManager.removeItem(bookId)
}
