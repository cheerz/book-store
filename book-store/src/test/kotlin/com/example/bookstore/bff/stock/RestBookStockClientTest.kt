package com.example.bookstore.bff.stock

import com.example.bookstore.web.observability.RequestIdFilter
import com.sun.net.httpserver.HttpServer
import org.slf4j.MDC
import java.net.InetSocketAddress
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RestBookStockClientTest {
    private var receivedRequestId: String? = null

    private val bookStock =
        HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
            createContext("/api/stock") { exchange ->
                receivedRequestId = exchange.requestHeaders.getFirst(RequestIdFilter.HEADER)
                val body = """[{"bookId":1,"quantity":3}]""".toByteArray()
                exchange.responseHeaders.add("Content-Type", "application/json")
                exchange.sendResponseHeaders(200, body.size.toLong())
                exchange.responseBody.use { it.write(body) }
            }
            start()
        }

    private val client = RestBookStockClient("http://127.0.0.1:${ bookStock.address.port }/api")

    @AfterTest
    fun stopBookStock() {
        bookStock.stop(0)
    }

    @Test
    fun `sends the request id of the current request to book-stock`() {
        MDC.put(RequestIdFilter.MDC_KEY, "6f1c2a52-6d5e-4d7a-9a4e-3c1f0b8e2d11")
        try {
            assertEquals(mapOf(1L to 3), client.quantitiesOf(listOf(1)))
        } finally {
            MDC.remove(RequestIdFilter.MDC_KEY)
        }

        assertEquals("6f1c2a52-6d5e-4d7a-9a4e-3c1f0b8e2d11", receivedRequestId)
    }
}
