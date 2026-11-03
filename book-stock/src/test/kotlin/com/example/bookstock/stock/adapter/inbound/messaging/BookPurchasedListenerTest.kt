package com.example.bookstock.stock.adapter.inbound.messaging

import com.example.bookstock.observability.RequestId
import com.example.bookstock.stock.domain.Stock
import com.example.bookstock.stock.domain.StockManager
import org.slf4j.MDC
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.listener.adapter.HandlerAdapter
import org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class BookPurchasedListenerTest {
    private class FakeStockManager : StockManager {
        val removals = mutableListOf<Triple<UUID, Long, Int>>()
        var requestIdInLogs: String? = null

        override fun getStock(bookId: Long): Stock = Stock.empty(bookId)

        override fun getStocks(bookIds: List<Long>): List<Stock> = bookIds.map(Stock::empty)

        override fun addItem(bookId: Long): Stock = Stock.empty(bookId)

        override fun removeItem(
            eventId: UUID,
            bookId: Long,
            quantity: Int,
        ): Stock {
            removals.add(Triple(eventId, bookId, quantity))
            requestIdInLogs = MDC.get(RequestId.MDC_KEY)
            return Stock.empty(bookId)
        }
    }

    private val stockManager = FakeStockManager()
    private val listener = BookPurchasedListener(stockManager)
    private val purchase = BookPurchasedMessage(UUID.randomUUID(), bookId = 42, quantity = 2, occurredAt = Instant.now())

    @Test
    fun `removes the purchased quantity from the stock of the book`() {
        listener.onBookPurchased(purchase, requestId = null)

        assertEquals(listOf(Triple(purchase.eventId, 42L, 2)), stockManager.removals)
    }

    // Goes through the Spring AMQP adapter: a listener returning a value makes it send a reply,
    // which fails without replyTo, and the purchase ends in the dead-letter queue after the retries.
    @Test
    fun `consumes a purchase without replying to book-store`() {
        val method = BookPurchasedListener::class.java.methods.single { it.name == "onBookPurchased" }
        val handlerMethodFactory = DefaultMessageHandlerMethodFactory().apply { afterPropertiesSet() }
        val converter = JacksonJsonMessageConverter()
        val adapter =
            MessagingMessageListenerAdapter(listener, method).apply {
                setHandlerAdapter(HandlerAdapter(handlerMethodFactory.createInvocableHandlerMethod(listener, method)))
                setMessageConverter(converter)
            }

        adapter.onMessage(converter.toMessage(purchase, MessageProperties()), null)

        assertEquals(listOf(Triple(purchase.eventId, 42L, 2)), stockManager.removals)
    }

    @Test
    fun `logs the purchase under the request id of book-store`() {
        val requestId = UUID.randomUUID().toString()

        listener.onBookPurchased(purchase, requestId)

        assertEquals(requestId, stockManager.requestIdInLogs)
    }
}
