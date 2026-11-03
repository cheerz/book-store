package com.example.bookstore.it.support

import com.example.bookstore.it.client.PagesApiClient
import com.example.bookstore.it.env.ReadinessGate
import org.junit.jupiter.api.BeforeAll
import java.time.Duration
import java.time.Instant
import java.util.UUID

abstract class IntegrationTest {
    protected val pages: PagesApiClient = PagesApiClient.create()

    protected fun uniqueTitle(prefix: String = "book"): String = "$prefix-${ UUID.randomUUID() }"

    // For effects applied asynchronously through the broker: retries the assertion until it holds.
    protected fun eventually(
        timeout: Duration = Duration.ofSeconds(10),
        assertion: () -> Unit,
    ) {
        val deadline = Instant.now().plus(timeout)
        while (true) {
            try {
                return assertion()
            } catch (failure: AssertionError) {
                if (Instant.now().isAfter(deadline)) throw failure
                Thread.sleep(200)
            }
        }
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun waitForApplication() {
            ReadinessGate.awaitReady()
        }
    }
}
