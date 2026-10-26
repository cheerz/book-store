package com.example.bookstore.web.error

enum class BbeError(
    val code: String,
    val summary: String,
    val httpStatus: Int,
    val userMessage: String,
) {
    SERVER_ERROR("BBE-0", "Server Error.", 500, "Une erreur est survenue, réessaie dans quelques instants."),
    BAD_REQUEST("BBE-1", "Bad request.", 400, "La demande est incomplète ou mal formée."),
    NOT_FOUND("BBE-7", "Not found.", 404, "Cette page n'existe pas."),
    INVALID_BOOK_DEFINITION("BBE-8", "Invalid book definition.", 422, "Ce format de livre n'est pas proposé."),
    INVALID_PRICE_DEFINITION("BBE-9", "Invalid price definition.", 422, "Le prix doit être positif ou nul."),
    UNSUPPORTED_BOOK_KIND("BBE-10", "Unsupported book kind.", 422, "Ce genre de livre n'est pas proposé."),
    METHOD_NOT_ALLOWED("BBE-12", "Bad endpoint.", 405, "Cette action n'est pas disponible."),
    UNSUPPORTED_MEDIA_TYPE("BBE-13", "Unsupported media type.", 415, "La demande est incomplète ou mal formée."),
    MISSING_PRODUCT_PRICE("BBE-14", "Missing product price.", 500, "Une erreur est survenue, réessaie dans quelques instants."),
    INVALID_BOOK_TITLE("BBE-15", "Invalid book title.", 422, "Le titre doit contenir entre 1 et 255 caractères."),
    ;

    val isServerError: Boolean get() = httpStatus >= 500
}
