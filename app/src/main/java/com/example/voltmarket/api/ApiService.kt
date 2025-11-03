package com.example.voltmarket.api

import com.example.voltmarket.model.*
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Interfaz que define todos los endpoints de la API de VoltMarket.
 * Retrofit usa esta interfaz para generar las implementaciones automáticamente.
 */
interface ApiService {

    // ============================================
    // AUTENTICACIÓN
    // ============================================

    /**
     * Login de usuario
     * POST /api/auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    /**
     * Registro de nuevo usuario
     * POST /api/auth/register
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // ============================================
    // USUARIOS
    // ============================================

    /**
     * Obtener perfil de usuario por ID
     * GET /api/users/{id}
     */
    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") userId: Long): User

    /**
     * Actualizar perfil del usuario actual
     * PUT /api/users/{id}
     */
    @PUT("api/users/{id}")
    suspend fun updateProfile(
        @Path("id") userId: Long,
        @Body request: UpdateProfileRequest
    ): User

    // ============================================
    // PRODUCTOS
    // ============================================

    /**
     * Obtener todos los productos (con filtros opcionales)
     * GET /api/products
     */
    @GET("products")
    suspend fun getProducts(
        @Query("categoryId") categoryId: Long? = null,
        @Query("q") search: String? = null,
        @Query("active") active: Boolean? = null
    ): List<Product>

    /**
     * Obtener un producto por ID
     * GET /api/products/{id}
     */
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Long): Product

    /**
     * Crear nuevo producto
     * POST /api/products
     */
    @POST("products")
    suspend fun createProduct(@Body request: ProductRequest): Product

    /**
     * Actualizar producto existente
     * PUT /api/products/{id}
     */
    @PUT("products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Long,
        @Body request: ProductRequest
    ): Product

    /**
     * Eliminar producto
     * DELETE /api/products/{id}
     */
    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") productId: Long): SuccessResponse

    /**
     * Obtener productos de un usuario específico
     * GET /api/products/user/{userId}
     */
    @GET("api/products/user/{userId}")
    suspend fun getProductsByUser(@Path("userId") userId: Long): List<Product>

    // ============================================
    // CATEGORÍAS
    // ============================================

    /**
     * Obtener todas las categorías
     * GET /api/products/categories
     */
    @GET("products/categories")
    suspend fun getCategories(): List<Category>

    /**
     * Obtener una categoría por ID
     * GET /api/categories/{id}
     */
    @GET("api/categories/{id}")
    suspend fun getCategoryById(@Path("id") categoryId: Long): Category

    // ============================================
    // FAVORITOS
    // ============================================

    /**
     * Obtener favoritos de un usuario específico
     * GET /api/favorites/{userId}
     */
    @GET("favorites/{userId}")
    suspend fun getFavoritesByUser(@Path("userId") userId: Long): List<Favorite>

    /**
     * Agregar producto a favoritos
     * POST /api/favorites/{userId}/{productId}
     */
    @POST("favorites/{userId}/{productId}")
    suspend fun addFavorite(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long
    ): Favorite

    /**
     * Quitar producto de favoritos
     * DELETE /api/favorites/{userId}/{productId}
     */
    @DELETE("favorites/{userId}/{productId}")
    suspend fun removeFavorite(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long
    ): SuccessResponse

    /**
     * Verificar si un producto está en favoritos
     * GET /api/favorites/{userId}/check/{productId}
     */
    @GET("favorites/{userId}/check/{productId}")
    suspend fun checkFavorite(
        @Path("userId") userId: Long,
        @Path("productId") productId: Long
    ): Map<String, Boolean>

    // ============================================
    // UPLOAD DE IMÁGENES
    // ============================================
    
    /**
     * Subir imagen de producto
     * POST /api/upload/image
     */
    @Multipart
    @POST("upload/image")
    suspend fun uploadImage(@Part image: MultipartBody.Part): Map<String, String>

    // ============================================
    // LIKES
    // ============================================

    /**
     * Dar like a un producto
     * POST /api/likes
     */
    @POST("api/likes")
    suspend fun addLike(@Body request: LikeRequest): Like

    /**
     * Quitar like de un producto
     * DELETE /api/likes/{productId}
     */
    @DELETE("api/likes/{productId}")
    suspend fun removeLike(@Path("productId") productId: Long): SuccessResponse

    /**
     * Obtener estadísticas de likes de un producto
     * GET /api/likes/product/{productId}
     */
    @GET("api/likes/product/{productId}")
    suspend fun getProductLikes(@Path("productId") productId: Long): LikesResponse

    /**
     * Verificar si el usuario dio like a un producto
     * GET /api/likes/check/{productId}
     */
    @GET("api/likes/check/{productId}")
    suspend fun isLiked(@Path("productId") productId: Long): Map<String, Boolean>

    // ============================================
    // COMENTARIOS
    // ============================================
    
    /**
     * Obtener comentarios de un producto
     * GET /api/comments/product/{productId}
     */
    @GET("comments/product/{productId}")
    suspend fun getProductComments(@Path("productId") productId: Long): List<Comment>
    
    /**
     * Crear comentario en un producto
     * POST /api/comments
     * El userId se obtiene automáticamente del token JWT en el backend
     */
    @POST("comments")
    suspend fun createComment(@Body request: CommentRequest): Comment
    
    /**
     * Eliminar comentario
     * DELETE /api/comments/{id}
     * Solo el dueño del comentario puede eliminarlo
     */
    @DELETE("comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: Long): SuccessResponse

    // ============================================
    // VALORACIONES
    // ============================================

    /**
     * Obtener valoraciones de un usuario
     * GET /api/ratings/user/{userId}
     */
    @GET("api/ratings/user/{userId}")
    suspend fun getUserRatings(@Path("userId") userId: Long): RatingStats

    /**
     * Crear valoración para un usuario
     * POST /api/ratings
     */
    @POST("api/ratings")
    suspend fun createRating(@Body request: RatingRequest): Rating

    /**
     * Verificar si ya valoré a un usuario
     * GET /api/ratings/check/{userId}
     */
    @GET("api/ratings/check/{userId}")
    suspend fun hasRatedUser(@Path("userId") userId: Long): Map<String, Boolean>
}