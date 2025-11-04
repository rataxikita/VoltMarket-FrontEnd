package com.example.voltmarket.model

import java.util.Locale

data class Product(
    val id: Long,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val marca: String?,
    val imageUrl: String?,
    val sku: String?,
    val activo: Boolean,
    val catId: Long?,
    val catNombre: String?
) {
    fun precioFormateado() = "$${String.format("%,.0f", precio)}"

    /**
     * Indica si el producto está disponible
     */
    fun isAvailable() = activo && stock > 0
    
    /**
     * ID de categoría compatible
     */
    val categoryId: Long?
        get() = catId
}

/**
 * Request para crear o actualizar un producto
 * Coincide con CreateProductRequest del backend
 */
data class ProductRequest(
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val marca: String?,
    val imageUrl: String?,
    val sku: String?,
    val activo: Boolean = true,
    val categoryId: Long? = null,
    val categoryNombre: String? = null
)

/**
 * Response con lista de productos (preparado para paginación)
 */
data class ProductsResponse(
    val products: List<Product>,
    val total: Int,
    val page: Int? = null,
    val totalPages: Int? = null
)

/**
 * Filtros para búsqueda de productos
 */
data class ProductFilters(
    val categoryId: Long? = null,
    val searchQuery: String? = null,
    val active: Boolean? = null
) {
    /**
     * Verifica si hay algún filtro activo
     */
    fun hasFilters() = categoryId != null || !searchQuery.isNullOrBlank() || active != null

    /**
     * Limpia todos los filtros
     */
    fun clear() = ProductFilters()
}