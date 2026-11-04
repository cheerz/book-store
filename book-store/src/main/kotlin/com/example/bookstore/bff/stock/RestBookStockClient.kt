package com.example.bookstore.bff.stock

import com.example.bookstore.web.observability.RequestIdFilter
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.body
import java.time.Duration

@Component
class RestBookStockClient(
    @Value("\${book-stock.base-url}") baseUrl: String,
) : BookStockClient {
    private val restClient =
        RestClient
            .builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactoryWithTimeouts())
            .requestInterceptor(::sendRequestId)
            .build()

    override fun quantitiesOf(bookIds: List<Long>): Map<Long, Int> {
        if (bookIds.isEmpty()) return emptyMap()
        val stocks = callBookStock { fetchStocks(bookIds) }
        return stocks.associate { it.bookId to it.quantity }
    }

    override fun addItem(bookId: Long): Int = callBookStock { postAddItem(bookId) }.quantity

    private fun fetchStocks(bookIds: List<Long>): List<StockResponseDto>? =
        restClient
            .get()
            .uri { it.path("/stock").queryParam("bookIds", bookIds.joinToString(",")).build() }
            .retrieve()
            .body<List<StockResponseDto>>()

    private fun postAddItem(bookId: Long): StockResponseDto? =
        restClient
            .post()
            .uri("/stock/{bookId}/add-item", bookId)
            .retrieve()
            .body<StockResponseDto>()

    private fun <T : Any> callBookStock(call: () -> T?): T =
        try {
            checkNotNull(call())
        } catch (exception: RestClientException) {
            throw StockUnavailableException(exception)
        }

    private fun requestFactoryWithTimeouts(): SimpleClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(TIMEOUT)
            setReadTimeout(TIMEOUT)
        }

    private fun sendRequestId(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        MDC.get(RequestIdFilter.MDC_KEY)?.let { request.headers.set(RequestIdFilter.HEADER, it) }
        return execution.execute(request, body)
    }

    private companion object {
        val TIMEOUT: Duration = Duration.ofSeconds(2)
    }
}
