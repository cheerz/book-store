package com.example.bookstore.format.domain

interface FormatManager {
    fun getFormats(): List<Format>

    fun formatOf(label: String): Format
}
