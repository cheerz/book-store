package com.example.bookstore.purchase.adapter.outbound.messaging

import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingTopology {
    @Bean
    fun bookStoreEvents(): TopicExchange = TopicExchange(BOOK_STORE_EVENTS_EXCHANGE)

    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()

    companion object {
        const val BOOK_STORE_EVENTS_EXCHANGE = "book-store.events"
        const val BOOK_PURCHASED_ROUTING_KEY = "book.purchased"
    }
}
