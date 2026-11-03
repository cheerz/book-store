package com.example.bookstock

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:hsqldb:mem:context-loads",
        "spring.rabbitmq.listener.simple.auto-startup=false",
    ],
)
class BookStockApplicationTests {
    @Test
    fun contextLoads() {
    }
}
