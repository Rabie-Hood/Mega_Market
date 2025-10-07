package ga.mega.market.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ga.mega.market.app.ui.screens.*
import ga.mega.market.app.viewmodels.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MegaMarketApp()
                }
            }
        }
    }
}

@Composable
fun MegaMarketApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val isLoggedIn by remember { derivedStateOf { authViewModel.getCurrentUser() != null } }

    NavHost(navController = navController, startDestination = if (isLoggedIn) "catalog" else "auth") {
        composable("auth") {
            AuthScreen(onAuthSuccess = { navController.navigate("catalog") })
        }
        composable("catalog") {
            ProductCatalogScreen(
                onProductClick = { product -> navController.navigate("details/${product.id}") }
            )
        }
        composable("details/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            ProductDetailsScreen(
                productId = productId,
                onAddToCart = { navController.navigate("cart") }
            )
        }
        composable("cart") {
            CartScreen(onCheckout = { navController.navigate("checkout") })
        }
        composable("checkout") {
            CheckoutScreen(onOrderPlaced = { navController.navigate("catalog") })
        }
    }
}