package com.example.bookstore

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["spring.datasource.url=jdbc:hsqldb:mem:context-loads"])
class BookStoreApplicationTests {
    @Test
    fun contextLoads() {
    }
}
