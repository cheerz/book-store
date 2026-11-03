package com.example.bookstock.stock.adapter.inbound.controller

import com.example.bookstock.stock.domain.StockManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/stock")
class StockController(
    private val stockManager: StockManager,
) {
    @GetMapping
    fun getStocks(
        @RequestParam bookIds: List<Long>,
    ): List<StockDto> = stockManager.getStocks(bookIds).map(StockDto::from)

    @GetMapping("/{bookId}")
    fun getStock(
        @PathVariable bookId: Long,
    ): StockDto = StockDto.from(stockManager.getStock(bookId))

    @PostMapping("/{bookId}/add-item")
    fun addItem(
        @PathVariable bookId: Long,
    ): StockDto = StockDto.from(stockManager.addItem(bookId))
}
