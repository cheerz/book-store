package com.example.demo_deploy.bff.error

enum class CbeError(val code: String, val summary: String, val httpStatus: Int) {
    SERVER_ERROR("CBE-0", "Server Error.", 500),
    BAD_REQUEST("CBE-1", "Bad request.", 400),
    INVALID_BOOK_DEFINITION("CBE-8", "Invalid book definition.", 422),
    INVALID_PRICE_DEFINITION("CBE-9", "Invalid price definition.", 422),
    UNSUPPORTED_BOOK_KIND("CBE-10", "Unsupported book kind.", 422),
}
