package com.example.demo_deploy.bff.catalog

data class CatalogPageDto(
    val books: List<CatalogPageItem>,
    val formats: List<FormatOption>,
)

data class FormatOption(
    val format: String,
    val priceInCents: Long,
)

data class CatalogPageItem(
    val title: String,
    val kind: String,
    val format: String,
    val bookPriceInCents: Long,
    val formatPriceInCents: Long,
    val totalPriceInCents: Long,
)
