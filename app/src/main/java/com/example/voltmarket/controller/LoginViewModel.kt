package com.example.voltmarket.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voltmarket.api.ApiService
import com.example.voltmarket.model.LoginRequest
import com.example.voltmarket.network.RetrofitProvider
import com.example.voltmarket.network.SharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true
)

class LoginViewModel(
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val apiService = RetrofitProvider.create(ApiService::class.java)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6

        _uiState.value = _uiState.value.copy(
            isEmailValid = isEmailValid,
            isPasswordValid = isPasswordValid
        )

        if (!isEmailValid || !isPasswordValid) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val request = LoginRequest(email = email, password = password)
                val response = apiService.login(request)

                // Guardar en SharedPreferences - CORREGIDO
                sharedPrefsManager.saveToken(response.token)
                sharedPrefsManager.saveUserId(response.userId)
                sharedPrefsManager.saveEmail(response.email)
                sharedPrefsManager.saveNombre(response.nombre)
                sharedPrefsManager.saveApellido(response.apellido)

                // Establecer token en RetrofitProvider para futuras peticiones
                RetrofitProvider.setToken(response.token)

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Credenciales inválidas o error de conexión"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}