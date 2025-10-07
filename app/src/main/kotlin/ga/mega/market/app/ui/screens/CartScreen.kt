package ga.mega.market.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ga.mega.market.app.models.CartItem
import ga.mega.market.app.models.Product
import ga.mega.market.app.viewmodels.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(onCheckout: () -> Unit, viewModel: CartViewModel = viewModel()) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val total by viewModel.total.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Shopping Cart") })
        },
        bottomBar = {
            if (cartWithProducts.isNotEmpty()) {
                BottomAppBar {
                    Text("Total: $${"%.2f".format(total)}", modifier = Modifier.weight(1f))
                    Button(onClick = onCheckout) {
                        Text("Checkout")
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else if (cartWithProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(cartWithProducts) { (product, cartItem) ->
                    CartItemRow(product = product, cartItem = cartItem, onUpdateQuantity = { qty ->
                        viewModel.updateQuantity(product.id, qty)
                    })
                }
            }
        }
    }
}

@Composable
fun CartItemRow(product: Product, cartItem: CartItem, onUpdateQuantity: (Int) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price} x ${cartItem.quantity} = $${"%.2f".format(product.price * cartItem.quantity)}")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (cartItem.quantity > 1) onUpdateQuantity(cartItem.quantity - 1) }) {
                    Text("-")
                }
                Text(cartItem.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = { onUpdateQuantity(cartItem.quantity + 1) }) {
                    Text("+")
                }
                IconButton(onClick = { onUpdateQuantity(0) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
        }
    }
}