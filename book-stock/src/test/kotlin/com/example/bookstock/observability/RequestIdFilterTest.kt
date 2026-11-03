package com.example.bookstock.observability

import jakarta.servlet.FilterChain
import org.slf4j.MDC
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class RequestIdFilterTest {
    private val filter = RequestIdFilter()

    private fun requestIdInLogsFor(sentRequestId: String?): String? {
        val request = MockHttpServletRequest().apply { sentRequestId?.let { addHeader(RequestId.HEADER, it) } }
        var requestIdInLogs: String? = null
        filter.doFilter(request, MockHttpServletResponse(), FilterChain { _, _ -> requestIdInLogs = MDC.get(RequestId.MDC_KEY) })
        return requestIdInLogs
    }

    @Test
    fun `logs the request under the request id sent by book-store`() {
        val sent = UUID.randomUUID().toString()

        assertEquals(sent, requestIdInLogsFor(sent))
    }

    @Test
    fun `never writes a malformed request id to the logs`() {
        val forged = "x\nERROR forged log line"

        assertNotEquals(forged, requestIdInLogsFor(forged))
    }

    @Test
    fun `does not leak the request id to the next request on the same thread`() {
        requestIdInLogsFor(UUID.randomUUID().toString())

        assertNull(MDC.get(RequestId.MDC_KEY))
    }
}
