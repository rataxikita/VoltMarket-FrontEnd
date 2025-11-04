package com.example.voltmarket.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voltmarket.api.ApiService
import com.example.voltmarket.model.*
import com.example.voltmarket.network.RetrofitProvider
import com.example.voltmarket.network.SharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedProduct: Product? = null,
    val comments: List<Comment> = emptyList(),
    val isLoadingComments: Boolean = false,
    val isFavorite: Boolean = false,
    val isLoadingFavorite: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

class ProductViewModel(
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val apiService = RetrofitProvider.create(ApiService::class.java)

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val products = apiService.getProducts()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun searchProducts(query: String, categoryId: Long?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val searchQuery = if (query.isBlank()) null else query
                val products = apiService.getProducts(
                    categoryId = categoryId,
                    search = searchQuery,
                    active = true
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    products = products
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error de búsqueda: ${e.message}"
                )
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = apiService.getCategories()
                _uiState.value = _uiState.value.copy(
                    categories = categories
                )
            } catch (e: Exception) {
                println("Error loading categories: ${e.message}")
            }
        }
    }

    fun loadProductDetail(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val product = apiService.getProductById(productId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedProduct = product
                )
                // Cargar estado de favorito
                checkFavoriteStatus(productId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun checkFavoriteStatus(productId: Long) {
        viewModelScope.launch {
            val userId = sharedPrefsManager.getUserId() ?: return@launch
            
            _uiState.value = _uiState.value.copy(isLoadingFavorite = true)
            try {
                val response = apiService.checkFavorite(userId, productId)
                val isFavorite = response["isFavorite"] ?: false
                _uiState.value = _uiState.value.copy(
                    isFavorite = isFavorite,
                    isLoadingFavorite = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isFavorite = false,
                    isLoadingFavorite = false
                )
            }
        }
    }

    fun createProduct(request: ProductRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            try {
                val product = apiService.createProduct(request)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Producto creado exitosamente",
                    error = null
                )
                loadProducts()
            } catch (e: retrofit2.HttpException) {
                val errorMessage = try {
                    e.response()?.errorBody()?.string() ?: "Error al crear producto"
                } catch (ex: Exception) {
                    "Error al crear producto: ${e.message}"
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al crear producto: ${e.message ?: "Error desconocido"}"
                )
            }
        }
    }

    fun uploadImage(imagePart: okhttp3.MultipartBody.Part, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val response = apiService.uploadImage(imagePart)
                val imageUrl = response["imageUrl"]
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Imagen subida exitosamente"
                )
                
                if (imageUrl != null) {
                    onSuccess(imageUrl)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al subir imagen: ${e.message}"
                )
            }
        }
    }

    fun toggleFavorite(productId: Long) {
        viewModelScope.launch {
            try {
                val userId = sharedPrefsManager.getUserId()
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        error = "Debes iniciar sesión para agregar favoritos"
                    )
                    return@launch
                }
                
                val currentFavoriteState = _uiState.value.isFavorite
                
                if (currentFavoriteState) {
                    // Quitar de favoritos
                    apiService.removeFavorite(userId, productId)
                    _uiState.value = _uiState.value.copy(
                        isFavorite = false,
                        successMessage = "Eliminado de favoritos"
                    )
                } else {
                    // Agregar a favoritos
                    apiService.addFavorite(userId, productId)
                    _uiState.value = _uiState.value.copy(
                        isFavorite = true,
                        successMessage = "Añadido a favoritos"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al modificar favorito: ${e.message}"
                )
            }
        }
    }

    fun toggleLike(productId: Long) {
        viewModelScope.launch {
            try {
                val request = LikeRequest(productId = productId)
                val like = apiService.addLike(request)
                loadProducts()
            } catch (e: Exception) {
                println("Error toggling like: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, successMessage = null)
    }
    
    fun loadProductComments(productId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingComments = true)
            try {
                val comments = apiService.getProductComments(productId)
                _uiState.value = _uiState.value.copy(
                    comments = comments,
                    isLoadingComments = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingComments = false,
                    error = "Error al cargar comentarios: ${e.message}"
                )
            }
        }
    }
    
    fun addComment(productId: Long, contenido: String) {
        if (contenido.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "El comentario no puede estar vacío")
            return
        }
        
        viewModelScope.launch {
            try {
                val request = CommentRequest(productId = productId, contenido = contenido)
                apiService.createComment(request)
                
                _uiState.value = _uiState.value.copy(
                    successMessage = "Comentario agregado"
                )
                
                // Recargar comentarios
                loadProductComments(productId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error al agregar comentario: ${e.message}"
                )
            }
        }
    }
}