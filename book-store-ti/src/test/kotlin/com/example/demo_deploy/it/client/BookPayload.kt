package com.example.demo_deploy.it.client

/**
 * Le contrat d'API, vu du client.
 *
 * Duplication volontaire de `BookDto` : le module ne dépend pas de
 * l'application. Si les deux divergent, c'est une rupture de contrat — et
 * c'est précisément ce que la suite doit détecter.
 */
data class BookPayload(
    val title: String,
    val bookKind: String,
)
