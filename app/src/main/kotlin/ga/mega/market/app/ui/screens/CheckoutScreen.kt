package ga.mega.market.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ga.mega.market.app.models.Address
import ga.mega.market.app.viewmodels.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(onOrderPlaced: () -> Unit, viewModel: CheckoutViewModel = viewModel()) {
    val cartWithProducts by viewModel.cartWithProducts.collectAsState()
    val total by viewModel.total.collectAsState()
    val shippingAddress by viewModel.shippingAddress.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val isPlacingOrder by viewModel.isPlacingOrder.collectAsState()
    val orderResult by viewModel.orderResult.collectAsState()

    var addressStreet by remember { mutableStateOf(shippingAddress.street) }
    var addressCity by remember { mutableStateOf(shippingAddress.city) }
    var addressState by remember { mutableStateOf(shippingAddress.state) }
    var addressZip by remember { mutableStateOf(shippingAddress.zipCode) }
    var addressCountry by remember { mutableStateOf(shippingAddress.country) }

    LaunchedEffect(orderResult) {
        if (orderResult?.isSuccess == true) {
            onOrderPlaced()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Checkout") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp)) {
            item {
                Text("Order Summary", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(cartWithProducts) { (product, cartItem) ->
                Text("${product.name} x ${cartItem.quantity} - $${"%.2f".format(product.price * cartItem.quantity)}")
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Subtotal: $${"%.2f".format(total)}")
                Text("Tax: $${"%.2f".format(total * 0.1)}")
                Text("Shipping: $5.00")
                Text("Total: $${"%.2f".format(total + total * 0.1 + 5.0)}", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("Shipping Address", style = MaterialTheme.typography.headlineSmall)
                OutlinedTextField(value = addressStreet, onValueChange = { addressStreet = it }, label = { Text("Street") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressCity, onValueChange = { addressCity = it }, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressState, onValueChange = { addressState = it }, label = { Text("State") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressZip, onValueChange = { addressZip = it }, label = { Text("Zip Code") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = addressCountry, onValueChange = { addressCountry = it }, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Text("Payment Method", style = MaterialTheme.typography.headlineSmall)
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = paymentMethod == "Credit Card", onClick = { viewModel.updatePaymentMethod("Credit Card") })
                    Text("Credit Card", modifier = Modifier.padding(start = 8.dp))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    RadioButton(selected = paymentMethod == "PayPal", onClick = { viewModel.updatePaymentMethod("PayPal") })
                    Text("PayPal", modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Button(
                    onClick = {
                        viewModel.updateShippingAddress(Address(addressStreet, addressCity, addressState, addressZip, addressCountry))
                        viewModel.placeOrder()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isPlacingOrder && paymentMethod.isNotEmpty() && addressStreet.isNotEmpty()
                ) {
                    Text(if (isPlacingOrder) "Placing Order..." else "Place Order")
                }
                orderResult?.exceptionOrNull()?.let { error ->
                    Text(error.message ?: "Error placing order", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}