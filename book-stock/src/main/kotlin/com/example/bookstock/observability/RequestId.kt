package com.example.bookstock.observability

import org.slf4j.MDC
import java.util.UUID

object RequestId {
    const val MDC_KEY = "requestId"
    const val HEADER = "X-Request-Id"

    private val UUID_FORMAT = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")

    // The id comes from the caller: only a UUID reaches the logs, anything else could forge log lines.
    fun fromCaller(sent: String?): String = sent?.takeIf(UUID_FORMAT::matches) ?: UUID.randomUUID().toString()

    fun <T> runWith(
        requestId: String,
        block: () -> T,
    ): T {
        MDC.put(MDC_KEY, requestId)
        try {
            return block()
        } finally {
            MDC.remove(MDC_KEY)
        }
    }
}
