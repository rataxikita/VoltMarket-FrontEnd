package com.example.voltmarket.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombre: String,
    val apellido: String
)

data class AuthResponse(
    val token: String,
    val type: String,  // "Bearer"
    val userId: Long,
    val email: String,
    val nombre: String,
    val apellido: String,
    val role: String
)