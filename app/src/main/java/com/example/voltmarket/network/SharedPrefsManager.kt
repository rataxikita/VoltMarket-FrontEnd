package com.example.voltmarket.network

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREFS_NAME = "voltmarket_prefs"
        private const val KEY_TOKEN = "token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_APELLIDO = "apellido"
        private const val KEY_AVATAR_URL = "avatar_url"
    }

    // Guardar token
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    // Obtener token
    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Guardar user ID
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }

    // Obtener user ID
    fun getUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1)
        return if (id == -1L) null else id
    }

    // Guardar email
    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    // Obtener email
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    // Guardar nombre
    fun saveNombre(nombre: String) {
        prefs.edit().putString(KEY_NOMBRE, nombre).apply()
    }

    // Obtener nombre
    fun getNombre(): String? {
        return prefs.getString(KEY_NOMBRE, null)
    }

    // Guardar apellido
    fun saveApellido(apellido: String) {
        prefs.edit().putString(KEY_APELLIDO, apellido).apply()
    }

    // Obtener apellido
    fun getApellido(): String? {
        return prefs.getString(KEY_APELLIDO, null)
    }

    // Guardar avatar URL
    fun saveAvatarUrl(avatarUrl: String) {
        prefs.edit().putString(KEY_AVATAR_URL, avatarUrl).apply()
    }

    // Obtener avatar URL
    fun getAvatarUrl(): String? {
        return prefs.getString(KEY_AVATAR_URL, null)
    }

    // Verificar si está logueado
    fun isLoggedIn(): Boolean {
        return getToken() != null && getUserId() != null
    }

    // Cerrar sesión (limpiar todo)
    fun logout() {
        prefs.edit().clear().apply()
        // También limpiar token de RetrofitProvider
        com.example.voltmarket.network.RetrofitProvider.setToken(null)
    }
}