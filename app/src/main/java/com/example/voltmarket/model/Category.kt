package com.example.voltmarket.model


/**
 * Modelo de categoría que coincide con CategoryResponse del backend
 */
data class Category(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    // Campo opcional que puede no venir del backend
    val icono: String? = null
)