package com.example.bookstore.web.observability

import jakarta.servlet.FilterChain
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RequestIdFilterTest {
    private val filter = RequestIdFilter()

    @Test
    fun `shares one request id between the logs and the response`() {
        val response = MockHttpServletResponse()
        var requestIdInLogs: String? = null

        filter.doFilter(MockHttpServletRequest(), response, FilterChain { _, _ -> requestIdInLogs = MDC.get(RequestIdFilter.MDC_KEY) })

        assertNotNull(requestIdInLogs)
        assertEquals(requestIdInLogs, response.getHeader(RequestIdFilter.HEADER))
    }

    @Test
    fun `does not leak the request id to the next request on the same thread`() {
        filter.doFilter(MockHttpServletRequest(), MockHttpServletResponse(), FilterChain { _, _ -> })

        assertNull(MDC.get(RequestIdFilter.MDC_KEY))
    }
}
