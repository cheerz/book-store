package com.example.demo_deploy.it.support

import com.example.demo_deploy.it.client.PagesApiClient
import com.example.demo_deploy.it.env.ReadinessGate
import java.util.UUID
import org.junit.jupiter.api.BeforeAll

abstract class IntegrationTest {

    protected val pages: PagesApiClient = PagesApiClient.create()

    protected fun uniqueTitle(prefix: String = "book"): String = "$prefix-${ UUID.randomUUID() }"

    companion object {
        @JvmStatic
        @BeforeAll
        fun waitForApplication() {
            ReadinessGate.awaitReady()
        }
    }
}
