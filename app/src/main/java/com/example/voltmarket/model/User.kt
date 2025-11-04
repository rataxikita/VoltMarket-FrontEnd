package com.example.voltmarket.model


data class User(
    val id: Long,
    val email: String,
    val nombre: String,
    val apellido: String,
    val avatarUrl: String?,
    val role: String,
    // TODO: Campos para implementar en la próxima versión actualizada - Auditoría de usuarios (fecha de creación y última actualización)
    // val createdAt: String?,
    // val updatedAt: String?
) {
    fun fullName() = "$nombre $apellido"

    fun initials() = "${nombre.firstOrNull() ?: ""}${apellido.firstOrNull() ?: ""}".uppercase()
}

data class UpdateProfileRequest(
    val nombre: String?,
    val apellido: String?,
    val avatarUrl: String?
)