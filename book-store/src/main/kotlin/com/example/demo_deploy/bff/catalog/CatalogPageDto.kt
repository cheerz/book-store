package com.example.demo_deploy.bff.catalog

data class CatalogPageDto(
    val books: List<CatalogPageItem>,
)

data class CatalogPageItem(
    val title: String,
    val kind: String,
    val size: String,
    val bookPriceInCents: Long,
    val sizePriceInCents: Long,
    val totalPriceInCents: Long,
)
