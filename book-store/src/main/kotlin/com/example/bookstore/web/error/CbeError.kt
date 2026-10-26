package com.example.bookstore.web.error

enum class CbeError(
    val code: String,
    val summary: String,
    val httpStatus: Int,
    val userMessage: String,
) {
    SERVER_ERROR("CBE-0", "Server Error.", 500, "Une erreur est survenue, réessaie dans quelques instants."),
    BAD_REQUEST("CBE-1", "Bad request.", 400, "La demande est incomplète ou mal formée."),
    NOT_FOUND("CBE-7", "Not found.", 404, "Cette page n'existe pas."),
    INVALID_BOOK_DEFINITION("CBE-8", "Invalid book definition.", 422, "Ce format de livre n'est pas proposé."),
    INVALID_PRICE_DEFINITION("CBE-9", "Invalid price definition.", 422, "Le prix doit être positif ou nul."),
    UNSUPPORTED_BOOK_KIND("CBE-10", "Unsupported book kind.", 422, "Ce genre de livre n'est pas proposé."),
    METHOD_NOT_ALLOWED("CBE-12", "Bad endpoint.", 405, "Cette action n'est pas disponible."),
}
