package com.example.bookstore.bff.stock

import com.example.bookstore.web.observability.RequestIdFilter
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
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
            .requestFactory(
                SimpleClientHttpRequestFactory().apply {
                    setConnectTimeout(TIMEOUT)
                    setReadTimeout(TIMEOUT)
                },
            ).requestInterceptor { request, body, execution ->
                MDC.get(RequestIdFilter.MDC_KEY)?.let { request.headers.set(RequestIdFilter.HEADER, it) }
                execution.execute(request, body)
            }.build()

    override fun quantitiesOf(bookIds: List<Long>): Map<Long, Int> {
        if (bookIds.isEmpty()) return emptyMap()
        val stocks =
            try {
                restClient
                    .get()
                    .uri { it.path("/stock").queryParam("bookIds", bookIds.joinToString(",")).build() }
                    .retrieve()
                    .body<List<StockResponseDto>>()
            } catch (exception: RestClientException) {
                throw StockUnavailableException(exception)
            }
        return checkNotNull(stocks).associate { it.bookId to it.quantity }
    }

    override fun addItem(bookId: Long): Int {
        val stock =
            try {
                restClient
                    .post()
                    .uri("/stock/{bookId}/add-item", bookId)
                    .retrieve()
                    .body<StockResponseDto>()
            } catch (exception: RestClientException) {
                throw StockUnavailableException(exception)
            }
        return checkNotNull(stock).quantity
    }

    private companion object {
        val TIMEOUT: Duration = Duration.ofSeconds(2)
    }
}
