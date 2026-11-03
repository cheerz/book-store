package com.example.bookstock.stock.domain

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class StockManagerImpl(
    private val stockRepository: StockRepository,
    private val processedEvents: ProcessedEventRepository,
) : StockManager {
    override fun getStock(bookId: Long): Stock = stockRepository.findByBookId(bookId) ?: Stock.empty(bookId)

    override fun getStocks(bookIds: List<Long>): List<Stock> {
        val stocksByBookId = stockRepository.findByBookIds(bookIds).associateBy { it.bookId }
        return bookIds.map { stocksByBookId[it] ?: Stock.empty(it) }
    }

    @Transactional
    override fun addItem(bookId: Long): Stock {
        val stock = stockRepository.findByBookIdForUpdate(bookId) ?: Stock.empty(bookId)
        return stockRepository.save(stock.withOneMoreItem())
    }

    // A purchase is already accepted by book-store when it reaches the stock: it is never refused,
    // a negative stock flags the books to restock.
    // The broker delivers at least once: the event id is recorded in the same transaction as the
    // stock movement, so a redelivered purchase is applied once. The check runs after the row lock:
    // two concurrent deliveries of one event queue on it, and the second sees the first one's mark.
    @Transactional
    override fun removeItem(
        eventId: UUID,
        bookId: Long,
        quantity: Int,
    ): Stock {
        val stock = stockRepository.findByBookIdForUpdate(bookId) ?: Stock.empty(bookId)
        if (processedEvents.isProcessed(eventId)) {
            logger.info("Purchase {} already applied, ignored", eventId)
            return stock
        }
        val updated = stockRepository.save(stock.withItemsRemoved(quantity))
        processedEvents.markProcessed(eventId)
        if (updated.isNegative) {
            logger.error("A traiter : stock du livre {} négatif ({}), réapprovisionner", bookId, updated.quantity)
        }
        return updated
    }

    private companion object {
        val logger = LoggerFactory.getLogger(StockManagerImpl::class.java)
    }
}
