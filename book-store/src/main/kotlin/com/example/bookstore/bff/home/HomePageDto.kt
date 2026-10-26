package com.example.bookstore.bff.home

data class HomePageDto(
    val bookCount: Int,
    val kinds: List<KindTile>,
    val latestTitles: List<String>,
)

data class KindTile(
    val kind: String,
    val bookCount: Int,
)
