package com.example.bookstore.purchase.adapter.outbound.messaging

import com.example.bookstore.purchase.domain.BookPurchased
import com.example.bookstore.purchase.domain.PurchaseNotRecordedException
import com.example.bookstore.web.observability.RequestIdFilter
import org.slf4j.MDC
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RabbitPurchaseEventPublisherTest {
    private class ConfirmingRabbitTemplate(
        private val isAcked: Boolean,
    ) : RabbitTemplate() {
        val sent = mutableListOf<Any>()
        var requestIdHeader: String? = null

        override fun convertAndSend(
            exchange: String?,
            routingKey: String?,
            message: Any,
            messagePostProcessor: MessagePostProcessor,
            correlationData: CorrelationData?,
        ) {
            sent.add(message)
            requestIdHeader =
                messagePostProcessor
                    .postProcessMessage(Message(ByteArray(0)))
                    .messageProperties
                    .getHeader(RequestIdFilter.HEADER)
            checkNotNull(correlationData).future.complete(CorrelationData.Confirm(isAcked, "queue full"))
        }
    }

    private val event = BookPurchased(UUID.randomUUID(), bookId = 1, quantity = 1, occurredAt = Instant.now())

    @Test
    fun `publishes the purchase once the broker confirms it`() {
        val rabbitTemplate = ConfirmingRabbitTemplate(isAcked = true)

        RabbitPurchaseEventPublisher(rabbitTemplate).publish(event)

        assertEquals(listOf<Any>(BookPurchasedMessage.from(event)), rabbitTemplate.sent)
    }

    @Test
    fun `sends the request id of the purchase with the event`() {
        val rabbitTemplate = ConfirmingRabbitTemplate(isAcked = true)

        MDC.put(RequestIdFilter.MDC_KEY, "6f1c2a52-6d5e-4d7a-9a4e-3c1f0b8e2d11")
        try {
            RabbitPurchaseEventPublisher(rabbitTemplate).publish(event)
        } finally {
            MDC.remove(RequestIdFilter.MDC_KEY)
        }

        assertEquals("6f1c2a52-6d5e-4d7a-9a4e-3c1f0b8e2d11", rabbitTemplate.requestIdHeader)
    }

    @Test
    fun `does not record the purchase when the broker refuses it`() {
        val rabbitTemplate = ConfirmingRabbitTemplate(isAcked = false)

        assertFailsWith<PurchaseNotRecordedException> { RabbitPurchaseEventPublisher(rabbitTemplate).publish(event) }
    }
}
