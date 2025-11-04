package com.example.voltmarket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voltmarket.controller.LoginViewModel
import com.example.voltmarket.controller.ProductViewModel
import com.example.voltmarket.controller.ProfileViewModel
import com.example.voltmarket.controller.RegisterViewModel
import com.example.voltmarket.network.SharedPrefsManager
import com.example.voltmarket.ui.theme.VoltMarketTheme
import com.example.voltmarket.view.auth.LoginScreen
import com.example.voltmarket.view.auth.RegisterScreen
import com.example.voltmarket.view.favorites.FavoritesScreen
import com.example.voltmarket.view.home.HomeScreen
import com.example.voltmarket.view.product.CreateProductScreen
import com.example.voltmarket.view.product.ProductDetailScreen
import com.example.voltmarket.view.profile.MyProductsScreen
import com.example.voltmarket.view.profile.ProfileScreen

class MainActivity : ComponentActivity() {

    private lateinit var sharedPrefsManager: SharedPrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sharedPrefsManager = SharedPrefsManager(this)

        // Inicializar RetrofitProvider con token guardado si existe
        sharedPrefsManager.getToken()?.let { token ->
            com.example.voltmarket.network.RetrofitProvider.setToken(token)
        }

        setContent {
            VoltMarketTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VoltMarketApp(sharedPrefsManager)
                }
            }
        }
    }
}

@Composable
fun VoltMarketApp(sharedPrefsManager: SharedPrefsManager) {
    val navController = rememberNavController()

    // ViewModels
    val loginViewModel = remember { LoginViewModel(sharedPrefsManager) }
    val registerViewModel = remember { RegisterViewModel(sharedPrefsManager) }
    val productViewModel = remember { ProductViewModel(sharedPrefsManager) }
    val profileViewModel = remember { ProfileViewModel(sharedPrefsManager) }

    // Determinar pantalla inicial según sesión
    val startDestination = if (sharedPrefsManager.isLoggedIn()) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Registro
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Home
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = productViewModel,
                sharedPrefsManager = sharedPrefsManager,
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCreateProductClick = {
                    navController.navigate(Screen.CreateProduct.route)
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    sharedPrefsManager.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla Detalle de Producto
        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            ProductDetailScreen(
                productId = productId,
                viewModel = productViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onViewSellerProfile = { sellerId ->
                    // Por ahora, navega al perfil del usuario actual
                    navController.navigate(Screen.Profile.route)
                }
            )
        }

        // Pantalla Crear Producto
        composable(Screen.CreateProduct.route) {
            CreateProductScreen(
                viewModel = productViewModel,
                sharedPrefsManager = sharedPrefsManager,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductCreated = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Perfil
        composable(Screen.Profile.route) {
            ProfileScreen(
                sharedPrefsManager = sharedPrefsManager,
                viewModel = profileViewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onLogout = {
                    sharedPrefsManager.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onMyProductsClick = {
                    navController.navigate(Screen.MyProducts.route)
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                },
                onSettingsClick = {
                    // TODO: Implementar pantalla de configuración
                }
            )
        }

        // Pantalla de Favoritos
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                sharedPrefsManager = sharedPrefsManager,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        // Pantalla Mis Productos
        composable(Screen.MyProducts.route) {
            MyProductsScreen(
                sharedPrefsManager = sharedPrefsManager,
                onBackClick = {
                    navController.popBackStack()
                },
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }
    }
}

// Sealed class para definir las rutas de navegación
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: Long) = "product/$productId"
    }
    object CreateProduct : Screen("create_product")
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
    object MyProducts : Screen("my_products")
}