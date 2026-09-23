package com.example.demo_deploy.it.support

import com.example.demo_deploy.it.client.BookApiClient
import com.example.demo_deploy.it.env.ReadinessGate
import java.util.UUID
import org.junit.jupiter.api.BeforeAll

/**
 * Base des scénarios : attend l'application, fournit le client et des données
 * uniques.
 *
 * Les titres uniques ne sont pas un détail : le dépôt de l'application est un
 * singleton mutable qu'une suite en boîte noire ne peut pas réinitialiser.
 * Assertions en "contient", jamais sur une taille de collection.
 */
abstract class IntegrationTest {

    protected val bookApi: BookApiClient = BookApiClient.create()

    protected fun uniqueTitle(prefix: String = "book"): String = "$prefix-${ UUID.randomUUID() }"

    companion object {
        @JvmStatic
        @BeforeAll
        fun waitForApplication() {
            ReadinessGate.awaitReady()
        }
    }
}
