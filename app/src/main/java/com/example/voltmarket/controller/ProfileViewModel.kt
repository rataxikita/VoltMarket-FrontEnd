package com.example.voltmarket.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voltmarket.api.ApiService
import com.example.voltmarket.network.RetrofitProvider
import com.example.voltmarket.network.SharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val favoritesCount: Int = 0,
    val productsCount: Int = 0,
    val error: String? = null
)

class ProfileViewModel(
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val apiService = RetrofitProvider.create(ApiService::class.java)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileStats()
    }

    fun loadProfileStats() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val userId = sharedPrefsManager.getUserId()
                if (userId == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Usuario no autenticado"
                    )
                    return@launch
                }

                // Obtener favoritos
                val favorites = try {
                    apiService.getFavoritesByUser(userId)
                } catch (e: Exception) {
                    emptyList()
                }

                // Obtener productos del usuario
                val products = try {
                    apiService.getProductsByUser(userId)
                } catch (e: Exception) {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favoritesCount = favorites.size,
                    productsCount = products.size
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar estadísticas: ${e.message}"
                )
            }
        }
    }

    fun refreshStats() {
        loadProfileStats()
    }
}

