package com.example.voltmarket.view.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.voltmarket.api.ApiService
import com.example.voltmarket.model.Favorite
import com.example.voltmarket.model.Product
import com.example.voltmarket.network.RetrofitProvider
import com.example.voltmarket.network.SharedPrefsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    sharedPrefsManager: SharedPrefsManager,
    onBackClick: () -> Unit,
    onProductClick: (Long) -> Unit
) {
    val apiService = RetrofitProvider.create(ApiService::class.java)
    val userId = sharedPrefsManager.getUserId()
    
    var favorites by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    var productsMap by remember { mutableStateOf<Map<Long, Product>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    
    // Cargar favoritos cuando se abre la pantalla
    LaunchedEffect(userId) {
        if (userId == null) {
            error = "Debes iniciar sesión para ver tus favoritos"
            isLoading = false
            return@LaunchedEffect
        }
        
        isLoading = true
        error = null
        try {
            val favoritesList = apiService.getFavoritesByUser(userId)
            favorites = favoritesList
            
            // Obtener los productos completos usando los productIds
            val productIds = favoritesList.map { it.productId }
            val productsList = mutableListOf<Product>()
            for (productId in productIds) {
                try {
                    val product = apiService.getProductById(productId)
                    productsList.add(product)
                } catch (e: Exception) {
                    // Si un producto no existe, simplemente lo omitimos
                    println("Error al cargar producto $productId: ${e.message}")
                }
            }
            productsMap = productsList.associateBy { it.id }
        } catch (e: Exception) {
            error = "Error al cargar favoritos: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
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
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6200EE))
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    if (userId != null) {
                                        isLoading = true
                                        error = null
                                        try {
                                            val favoritesList = apiService.getFavoritesByUser(userId)
                                            favorites = favoritesList
                                            
                                            // Obtener los productos completos
                                            val productIds = favoritesList.map { it.productId }
                                            val productsList = mutableListOf<Product>()
                                            for (productId in productIds) {
                                                try {
                                                    val product = apiService.getProductById(productId)
                                                    productsList.add(product)
                                                } catch (e: Exception) {
                                                    println("Error al cargar producto $productId: ${e.message}")
                                                }
                                            }
                                            productsMap = productsList.associateBy { it.id }
                                        } catch (e: Exception) {
                                            error = "Error al cargar favoritos: ${e.message}"
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
            favorites.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "No tienes favoritos aún",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            text = "Agrega productos a tus favoritos para verlos aquí",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF5F5F5)),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(favorites) { favorite ->
                        productsMap[favorite.productId]?.let { product ->
                            FavoriteProductCard(
                                product = product,
                                onClick = { onProductClick(product.id) },
                                onRemoveFavorite = {
                                    scope.launch {
                                        try {
                                            apiService.removeFavorite(userId!!, product.id)
                                            favorites = favorites.filter { it.id != favorite.id }
                                            // También remover del mapa de productos
                                            productsMap = productsMap.filterKeys { it != product.id }
                                        } catch (e: Exception) {
                                            error = "Error al eliminar favorito: ${e.message}"
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteProductCard(
    product: Product,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen del producto
            AsyncImage(
                model = product.imageUrl ?: "https://via.placeholder.com/400x300?text=Sin+Imagen",
                contentDescription = product.nombre,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            // Información del producto
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = product.descripcion ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = product.precioFormateado(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
            }
            
            // Botón para eliminar de favoritos
            IconButton(
                onClick = onRemoveFavorite,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = Color(0xFFE91E63)
                )
            }
        }
    }
}

