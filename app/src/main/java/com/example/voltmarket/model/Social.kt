package com.example.voltmarket.model



data class Favorite(
    val id: Long,
    val userId: Long,
    val productId: Long,
    // TODO: Campo para implementar en la próxima versión actualizada - Fecha de creación del favorito
    // val createdAt: String?,

    // Producto completo (incluido en algunas respuestas)
    val product: Product? = null
)

/**
 * Request para agregar/quitar favorito
 */
data class FavoriteRequest(
    val productId: Long
)

// ============================================
// LIKES
// ============================================

/**
 * Like en un producto
 */
data class Like(
    val id: Long,
    val userId: Long,
    val productId: Long,
    // TODO: Campo para implementar en la próxima versión actualizada - Fecha de creación del like
    // val createdAt: String?
)

/**
 * Request para dar/quitar like
 */
data class LikeRequest(
    val productId: Long
)

/**
 * Response con estadísticas de likes de un producto
 */
data class LikesResponse(
    val count: Int,
    val isLiked: Boolean
) {
    /**
     * Texto para mostrar cantidad de likes
     */
    fun likesText() = when(count) {
        0 -> "Sin likes"
        1 -> "1 like"
        else -> "$count likes"
    }
}

// ============================================
// COMENTARIOS
// ============================================

/**
 * Comentario en un producto
 */
data class Comment(
    val id: Long,
    val userId: Long,
    val productId: Long,
    val contenido: String,
    val createdAt: String?,
    // TODO: Campo para implementar en la próxima versión actualizada - Detectar si un comentario fue editado
    // val updatedAt: String?,

    // Usuario que hizo el comentario (incluido en algunas respuestas)
    val user: User? = null
) {
    /**
     * Tiempo relativo del comentario
     * TODO: Implementar cálculo real de tiempo relativo (hace 5 minutos, hace 2 horas, etc.)
     */
    fun timeAgo(): String {
        return createdAt ?: "Hace un momento"
    }

    /**
     * Verifica si el comentario fue editado
     * TODO: Implementar en la próxima versión actualizada cuando se habilite la edición de comentarios
     */
    // fun wasEdited() = updatedAt != null && updatedAt != createdAt
}

/**
 * Request para crear un comentario
 */
data class CommentRequest(
    val productId: Long,
    val contenido: String
)

// ============================================
// VALORACIONES
// TODO: Funcionalidad para implementar en la próxima versión actualizada
// Sistema de valoraciones de usuarios vendedores - Permite a los usuarios valorar a los vendedores (1-5 estrellas)
// ============================================

/**
 * Valoración de un usuario vendedor
 * TODO: Implementar en la próxima versión actualizada - Permite a los usuarios valorar a los vendedores después de una compra
 */
/*
data class Rating(
    val id: Long,
    val ratedUserId: Long,
    val raterUserId: Long,
    val puntuacion: Int, // 1-5 estrellas
    val comentario: String?,
    val createdAt: String?,

    // Usuario que hizo la valoración (incluido en algunas respuestas)
    val raterUser: User? = null
) {
    /**
     * Genera un string con las estrellas visuales
     */
    fun starsText() = "⭐".repeat(puntuacion)
}
*/

/**
 * Request para valorar a un usuario
 * TODO: Implementar en la próxima versión actualizada
 */
/*
data class RatingRequest(
    val ratedUserId: Long,
    val puntuacion: Int, // 1-5
    val comentario: String?
)
*/

/**
 * Estadísticas de valoraciones de un usuario
 * TODO: Implementar en la próxima versión actualizada - Muestra promedio, total de valoraciones y lista completa
 */
/*
data class RatingStats(
    val userId: Long,
    val averageRating: Double,
    val totalRatings: Int,
    val ratings: List<Rating> = emptyList()
) {
    /**
     * Promedio formateado con 1 decimal
     * Ejemplo: 4.5
     */
    fun averageFormatted() = String.format("%.1f", averageRating)

    /**
     * Número de estrellas completas
     */
    fun fullStars() = averageRating.toInt()

    /**
     * Indica si tiene media estrella
     */
    fun hasHalfStar() = (averageRating - fullStars()) >= 0.5

    /**
     * Texto para mostrar valoraciones
     */
    fun ratingsText() = when(totalRatings) {
        0 -> "Sin valoraciones"
        1 -> "1 valoración"
        else -> "$totalRatings valoraciones"
    }
}
*/