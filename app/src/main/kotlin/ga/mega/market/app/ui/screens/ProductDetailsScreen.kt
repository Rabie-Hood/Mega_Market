package ga.mega.market.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ga.mega.market.app.models.Product
import ga.mega.market.app.viewmodels.ProductDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(productId: String, onAddToCart: () -> Unit, viewModel: ProductDetailsViewModel = viewModel()) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val addToCartResult by viewModel.addToCartResult.collectAsState()

    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    LaunchedEffect(addToCartResult) {
        if (addToCartResult?.isSuccess == true) {
            onAddToCart()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Details") })
        }
    ) { padding ->
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            product?.let { prod ->
                Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                    LazyRow {
                        items(prod.images) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = prod.name,
                                modifier = Modifier.size(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(prod.name, style = MaterialTheme.typography.headlineMedium)
                    Text("$${prod.price}", style = MaterialTheme.typography.titleLarge)
                    if (prod.originalPrice != null) {
                        Text("Original: $${prod.originalPrice}", style = MaterialTheme.typography.bodyMedium)
                    }
                    Text(prod.description, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { viewModel.addToCart() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Add to Cart")
                    }

                    addToCartResult?.exceptionOrNull()?.let { error ->
                        Text(error.message ?: "Error adding to cart", color = MaterialTheme.colorScheme.error)
                    }
                }
            } ?: Text("Product not found", modifier = Modifier.padding(padding))
        }
    }
}