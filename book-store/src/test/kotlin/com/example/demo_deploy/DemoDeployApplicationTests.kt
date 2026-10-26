package com.example.demo_deploy

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

// Base en mémoire : sinon le test écrirait la base fichier de dev dans book-store/data.
@SpringBootTest(properties = ["spring.datasource.url=jdbc:hsqldb:mem:context-loads"])
class DemoDeployApplicationTests {
    @Test
    fun contextLoads() {
    }
}
