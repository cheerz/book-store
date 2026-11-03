package com.example.bookstock.stock.adapter.inbound.messaging

import com.example.bookstock.observability.RequestId
import com.example.bookstock.stock.domain.StockManager
import org.springframework.amqp.AmqpRejectAndDontRequeueException
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class BookPurchasedListener(
    private val stockManager: StockManager,
) {
    // Must return Unit: Spring AMQP sends any returned value as a reply, which fails without replyTo
    // and dead-letters the purchase after the retries.
    @RabbitListener(queues = [MessagingTopology.BOOK_PURCHASED_QUEUE])
    fun onBookPurchased(
        message: BookPurchasedMessage,
        @Header(RequestId.HEADER, required = false) requestId: String?,
    ) {
        RequestId.runWith(RequestId.fromCaller(requestId)) {
            if (message.quantity < 1) throw AmqpRejectAndDontRequeueException("Purchase of ${ message.quantity } item(s)")
            stockManager.removeItem(message.eventId, message.bookId, message.quantity)
        }
    }
}
