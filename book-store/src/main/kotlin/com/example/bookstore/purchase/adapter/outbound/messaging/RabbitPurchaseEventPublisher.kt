package com.example.bookstore.purchase.adapter.outbound.messaging

import com.example.bookstore.purchase.domain.BookPurchased
import com.example.bookstore.purchase.domain.PurchaseEventPublisher
import com.example.bookstore.purchase.domain.PurchaseNotRecordedException
import com.example.bookstore.web.observability.RequestIdFilter
import org.slf4j.MDC
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Component
class RabbitPurchaseEventPublisher(
    private val rabbitTemplate: RabbitTemplate,
) : PurchaseEventPublisher {
    // [DEC:publisher-confirms] Returns only once RabbitMQ acks the message (publisher confirms, spring.rabbitmq.publisher-confirm-type):
    // without the wait, convertAndSend returns as soon as the frame is written and a lost message raises nothing.
    override fun publish(event: BookPurchased) {
        val correlation = CorrelationData(event.eventId.toString())
        try {
            rabbitTemplate.convertAndSend(
                MessagingTopology.BOOK_STORE_EVENTS_EXCHANGE,
                MessagingTopology.BOOK_PURCHASED_ROUTING_KEY,
                BookPurchasedMessage.from(event),
                ::withRequestId,
                correlation,
            )
            val confirm = correlation.future.get(CONFIRM_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            if (!confirm.isAck) throw AmqpException("RabbitMQ refused purchase ${ event.eventId }: ${ confirm.reason }")
        } catch (exception: AmqpException) {
            throw PurchaseNotRecordedException(exception)
        } catch (exception: TimeoutException) {
            throw PurchaseNotRecordedException(exception)
        }
    }

    private fun withRequestId(message: Message): Message =
        message.apply { MDC.get(RequestIdFilter.MDC_KEY)?.let { messageProperties.setHeader(RequestIdFilter.HEADER, it) } }

    private companion object {
        const val CONFIRM_TIMEOUT_SECONDS = 2L
    }
}
