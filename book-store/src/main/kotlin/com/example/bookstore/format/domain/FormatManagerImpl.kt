package com.example.bookstore.format.domain

import org.springframework.stereotype.Service

@Service
class FormatManagerImpl(
    private val formatRepository: FormatRepository,
) : FormatManager {
    override fun getFormats(): List<Format> = formatRepository.findAll()

    override fun formatOf(label: String): Format {
        val formats = getFormats()

        return formats.find { it.label == normalize(label) }
            ?: throw UnknownFormatException(label, formats.map { it.label })
    }

    private fun normalize(label: String): String = label.trim().lowercase()
}
