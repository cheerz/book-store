package com.example.demo_deploy.it.scenario

import com.example.demo_deploy.it.support.IntegrationTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Le seul scénario fourni avec le squelette : il vérifie que la plomberie
 * tient debout (application joignable, client Feign fonctionnel, contrat JSON
 * décodable). Les scénarios métier vont à côté, dans ce même package.
 */
class SmokeIT : IntegrationTest() {

    @Test
    fun `the catalogue is reachable`() {
        assertTrue(bookApi.listBooks().isNotEmpty())
    }
}
