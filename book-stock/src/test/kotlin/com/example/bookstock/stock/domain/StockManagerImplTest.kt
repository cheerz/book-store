package com.example.bookstock.stock.domain

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class StockManagerImplTest {
    private class FakeStockRepository(
        vararg stocks: Stock,
    ) : StockRepository {
        private val stored = stocks.associateBy { it.bookId }.toMutableMap()

        override fun findByBookId(bookId: Long): Stock? = stored[bookId]

        override fun findByBookIds(bookIds: List<Long>): List<Stock> = bookIds.mapNotNull(stored::get)

        override fun findByBookIdForUpdate(bookId: Long): Stock? = stored[bookId]

        override fun save(stock: Stock): Stock = stock.also { stored[it.bookId] = it }
    }

    private class FakeProcessedEventRepository : ProcessedEventRepository {
        private val processed = mutableSetOf<UUID>()

        override fun isProcessed(eventId: UUID): Boolean = eventId in processed

        override fun markProcessed(eventId: UUID) {
            processed.add(eventId)
        }
    }

    private fun stockManager(repository: StockRepository) = StockManagerImpl(repository, FakeProcessedEventRepository())

    private val purchaseId = UUID.randomUUID()

    @Test
    fun `gives the stock of a book`() {
        val stockManager = stockManager(FakeStockRepository(Stock(bookId = 1, quantity = 4)))

        assertEquals(Stock(bookId = 1, quantity = 4), stockManager.getStock(1))
    }

    @Test
    fun `gives an empty stock for a book never stocked`() {
        assertEquals(Stock(bookId = 1, quantity = 0), stockManager(FakeStockRepository()).getStock(1))
    }

    @Test
    fun `gives the stock of several books, empty for a book never stocked`() {
        val stockManager = stockManager(FakeStockRepository(Stock(bookId = 1, quantity = 4)))

        assertEquals(listOf(Stock(bookId = 1, quantity = 4), Stock(bookId = 2, quantity = 0)), stockManager.getStocks(listOf(1, 2)))
    }

    @Test
    fun `adds an item to the stock of a book`() {
        val repository = FakeStockRepository(Stock(bookId = 1, quantity = 2))

        val stock = stockManager(repository).addItem(1)

        assertEquals(Stock(bookId = 1, quantity = 3), stock)
        assertEquals(stock, repository.findByBookId(1))
    }

    @Test
    fun `opens the stock of a book on its first item`() {
        val repository = FakeStockRepository()

        val stock = stockManager(repository).addItem(1)

        assertEquals(Stock(bookId = 1, quantity = 1), stock)
        assertEquals(stock, repository.findByBookId(1))
    }

    @Test
    fun `removes the purchased items from the stock of a book`() {
        val repository = FakeStockRepository(Stock(bookId = 1, quantity = 5))

        val stock = stockManager(repository).removeItem(purchaseId, bookId = 1, quantity = 2)

        assertEquals(Stock(bookId = 1, quantity = 3), stock)
        assertEquals(stock, repository.findByBookId(1))
    }

    @Test
    fun `applies a purchase delivered twice only once`() {
        val repository = FakeStockRepository(Stock(bookId = 1, quantity = 5))
        val stockManager = stockManager(repository)

        stockManager.removeItem(purchaseId, bookId = 1, quantity = 2)
        val stock = stockManager.removeItem(purchaseId, bookId = 1, quantity = 2)

        assertEquals(Stock(bookId = 1, quantity = 3), stock)
        assertEquals(stock, repository.findByBookId(1))
    }

    @Test
    fun `honours a purchase beyond the stock by letting it go negative`() {
        val repository = FakeStockRepository(Stock(bookId = 1, quantity = 1))

        val stock = stockManager(repository).removeItem(purchaseId, bookId = 1, quantity = 2)

        assertEquals(Stock(bookId = 1, quantity = -1), stock)
        assertEquals(stock, repository.findByBookId(1))
    }
}
