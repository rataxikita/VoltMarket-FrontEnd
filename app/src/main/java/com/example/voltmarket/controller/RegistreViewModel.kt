package com.example.voltmarket.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voltmarket.api.ApiService
import com.example.voltmarket.model.RegisterRequest
import com.example.voltmarket.network.RetrofitProvider
import com.example.voltmarket.network.SharedPrefsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isNombreValid: Boolean = true,
    val isApellidoValid: Boolean = true,
    val passwordsMatch: Boolean = true
)

class RegisterViewModel(
    private val sharedPrefsManager: SharedPrefsManager
) : ViewModel() {

    private val apiService = RetrofitProvider.create(ApiService::class.java)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        nombre: String,
        apellido: String,
        onSuccess: () -> Unit
    ) {
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 6
        val isNombreValid = nombre.isNotBlank()
        val isApellidoValid = apellido.isNotBlank()
        val passwordsMatch = password == confirmPassword

        _uiState.value = _uiState.value.copy(
            isEmailValid = isEmailValid,
            isPasswordValid = isPasswordValid,
            isNombreValid = isNombreValid,
            isApellidoValid = isApellidoValid,
            passwordsMatch = passwordsMatch
        )

        if (!isEmailValid || !isPasswordValid || !isNombreValid ||
            !isApellidoValid || !passwordsMatch) {
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    nombre = nombre,
                    apellido = apellido
                )
                val response = apiService.register(request)

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
                println("❌ ERROR REGISTRO:")
                println("Tipo: ${e.javaClass.simpleName}")
                println("Mensaje: ${e.message}")
                e.printStackTrace()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al registrar: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}