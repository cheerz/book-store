package com.example.bookstock.stock.adapter.inbound.messaging

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Declarables
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingTopology {
    @Bean
    fun bookPurchasedTopology(): Declarables {
        val bookStoreEvents = TopicExchange(BOOK_STORE_EVENTS_EXCHANGE)
        val deadLetters = FanoutExchange(DEAD_LETTER_EXCHANGE)
        val bookPurchased =
            QueueBuilder
                .durable(BOOK_PURCHASED_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .build()
        val bookPurchasedDeadLetters = QueueBuilder.durable("$BOOK_PURCHASED_QUEUE.dlq").build()

        return Declarables(
            bookStoreEvents,
            deadLetters,
            bookPurchased,
            bookPurchasedDeadLetters,
            BindingBuilder.bind(bookPurchased).to(bookStoreEvents).with(BOOK_PURCHASED_ROUTING_KEY),
            bindToDeadLetters(bookPurchasedDeadLetters, deadLetters),
        )
    }

    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()

    private fun bindToDeadLetters(
        queue: Queue,
        deadLetters: FanoutExchange,
    ): Binding = BindingBuilder.bind(queue).to(deadLetters)

    companion object {
        const val BOOK_STORE_EVENTS_EXCHANGE = "book-store.events"
        const val BOOK_PURCHASED_ROUTING_KEY = "book.purchased"
        const val BOOK_PURCHASED_QUEUE = "book-stock.book-purchased"
        const val DEAD_LETTER_EXCHANGE = "book-stock.dead-letters"
    }
}
