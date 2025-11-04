package com.example.voltmarket.view.product

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.voltmarket.controller.ProductViewModel
import com.example.voltmarket.model.ProductRequest
import com.example.voltmarket.network.SharedPrefsManager
import com.example.voltmarket.util.ImageUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProductScreen(
    viewModel: ProductViewModel,
    sharedPrefsManager: SharedPrefsManager,
    onBackClick: () -> Unit,
    onProductCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("1") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var expandedCategory by remember { mutableStateOf(false) }

    var nombreError by remember { mutableStateOf<String?>(null) }
    var precioError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    val context = LocalContext.current
    
    // Launcher para seleccionar imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Subir imagen automáticamente
            val multipartBody = ImageUtil.uriToMultipartBody(context, it)
            if (multipartBody != null) {
                viewModel.uploadImage(multipartBody) { uploadedUrl ->
                    imageUrl = uploadedUrl
                }
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            // Esperar un momento para que el usuario vea el mensaje de éxito
            kotlinx.coroutines.delay(1500)
            viewModel.clearSuccessMessage()
            // Recargar productos en el perfil
            onProductCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Publicar Producto",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Información del producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    nombreError = null
                },
                label = { Text("Nombre del producto *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError != null,
                supportingText = nombreError?.let { { Text(it, color = Color.Red) } }
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5
            )

            OutlinedTextField(
                value = precio,
                onValueChange = {
                    if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                        precio = it
                        precioError = null
                    }
                },
                label = { Text("Precio *") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Text("$", style = MaterialTheme.typography.titleMedium) },
                isError = precioError != null,
                supportingText = precioError?.let { { Text(it, color = Color.Red) } }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it },
                    label = { Text("Marca") },
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = stock,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d+$"))) {
                            stock = it
                        }
                    },
                    label = { Text("Stock") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = it }
            ) {
                OutlinedTextField(
                    value = uiState.categories.find { it.id == selectedCategoryId }?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría *") },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = categoryError != null,
                    supportingText = categoryError?.let { { Text(it, color = Color.Red) } }
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    uiState.categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(category.icono ?: "📦")
                                    Text(category.nombre)
                                }
                            },
                            onClick = {
                                selectedCategoryId = category.id
                                categoryError = null
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("URL directa de imagen (.jpg, .png, .webp)") },
                supportingText = {
                    if (imageUrl.isNotEmpty() && !imageUrl.matches(Regex(".*\\.(jpg|jpeg|png|gif|webp|JPG|JPEG|PNG|GIF|WEBP)(\\?.*)?$"))) {
                        Text(
                            text = "⚠️ Usa una URL directa de imagen (debe terminar en .jpg, .png, .webp, etc.)",
                            color = Color(0xFFFF9800)
                        )
                    } else if (imageUrl.isNotEmpty()) {
                        Text(
                            text = "✓ URL de imagen válida",
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            )
            
            // Botón para seleccionar imagen desde galería
            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF6200EE)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Seleccionar imagen",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar desde Galería")
            }
            
            if (selectedImageUri != null) {
                Text(
                    text = "✓ Imagen seleccionada",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    var isValid = true

                    if (nombre.isBlank()) {
                        nombreError = "El nombre es requerido"
                        isValid = false
                    }

                    if (precio.isBlank()) {
                        precioError = "El precio es requerido"
                        isValid = false
                    } else if (precio.toDoubleOrNull() == null || precio.toDouble() <= 0) {
                        precioError = "Precio inválido"
                        isValid = false
                    }

                    if (selectedCategoryId == null) {
                        categoryError = "Selecciona una categoría"
                        isValid = false
                    }

                    if (isValid) {
                       val request = ProductRequest(
                        nombre = nombre,
                        descripcion = descripcion.ifBlank { null },
                        precio = precio.toDouble(),
                        stock = stock.toIntOrNull() ?: 1,
                        marca = marca.ifBlank { null },
                        imageUrl = imageUrl.ifBlank { null },
                        sku = null,  // ← Enviar null para que el backend lo genere
                        activo = true,
                        categoryId = selectedCategoryId
                        )
                        viewModel.createProduct(request)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Publicar Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFC62828)
                        )
                        Text(
                            text = error,
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFFC62828)
                            )
                        }
                    }
                }
            }

            uiState.successMessage?.let { message ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Text(
                            text = message,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "💡 Consejos para tu publicación:",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "• Usa un nombre descriptivo y claro",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "• Incluye todos los detalles en la descripción",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )
                    Text(
                        text = "• Añade fotos de buena calidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}