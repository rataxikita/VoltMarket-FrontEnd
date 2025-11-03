package com.example.voltmarket.model

data class SuccessResponse(
    val success: Boolean,
    val message: String
)

data class ErrorResponse(
    val error: String,
    val message: String,
    val statusCode: Int? = null
)

enum class SortOption(val value: String, val displayName: String) {
    NEWEST("newest", "Más recientes"),
    PRICE_ASC("price_asc", "Precio: menor a mayor"),
    PRICE_DESC("price_desc", "Precio: mayor a menor"),
    POPULAR("popular", "Más populares");

    companion object {
        fun fromValue(value: String) = values().find { it.value == value } ?: NEWEST

        fun all() = values().toList()
    }
}