package com.example.voltmarket.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

object ImageUtil {
    
    /**
     * Convierte una URI de imagen a un MultipartBody.Part para subir al servidor
     */
    fun uriToMultipartBody(context: Context, uri: Uri, paramName: String = "image"): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // Crear archivo temporal
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            val outputStream = FileOutputStream(tempFile)
            
            // Copiar contenido
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            
            // Crear RequestBody
            val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            
            // Crear MultipartBody.Part
            MultipartBody.Part.createFormData(paramName, tempFile.name, requestBody)
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Obtiene el nombre del archivo desde una URI
     */
    fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        
        return fileName ?: "image_${System.currentTimeMillis()}.jpg"
    }
}

