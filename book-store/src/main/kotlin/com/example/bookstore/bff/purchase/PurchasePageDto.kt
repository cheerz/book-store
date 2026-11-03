package com.example.bookstore.bff.purchase

data class PurchasePageDto(
    val books: List<PurchasePageItem>,
)

data class PurchasePageItem(
    val id: Long,
    val title: String,
    val kind: String,
    val format: String,
    val totalPriceInCents: Long,
    val stock: Int,
)
