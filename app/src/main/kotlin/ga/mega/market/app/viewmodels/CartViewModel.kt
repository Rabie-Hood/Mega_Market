package ga.mega.market.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ga.mega.market.app.models.CartItem
import ga.mega.market.app.models.Product
import ga.mega.market.app.repositories.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CartViewModel(private val firestoreRepository: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems: StateFlow<Map<String, CartItem>> = _cartItems

    private val _products = MutableStateFlow<Map<String, Product>>(emptyMap())
    val products: StateFlow<Map<String, Product>> = _products

    val cartWithProducts: StateFlow<List<Pair<Product, CartItem>>> = combine(_cartItems, _products) { cart, prods ->
        cart.mapNotNull { (productId, cartItem) ->
            prods[productId]?.let { it to cartItem }
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total

    init {
        loadCart()
    }

    private fun loadCart() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            val result = firestoreRepository.getCart(userId)
            if (result.isSuccess) {
                val cart = result.getOrNull() ?: emptyMap()
                _cartItems.value = cart
                loadProducts(cart.keys)
            }
            _isLoading.value = false
        }
    }

    private fun loadProducts(productIds: Set<String>) {
        viewModelScope.launch {
            val prods = mutableMapOf<String, Product>()
            for (id in productIds) {
                val result = firestoreRepository.getProduct(id)
                if (result.isSuccess) {
                    result.getOrNull()?.let { prods[id] = it }
                }
            }
            _products.value = prods
            calculateTotal()
        }
    }

    private fun calculateTotal() {
        val total = cartWithProducts.value.sumOf { (product, cartItem) ->
            product.price * cartItem.quantity
        }
        _total.value = total
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val result = firestoreRepository.updateCartItem(userId, productId, quantity)
            if (result.isSuccess) {
                loadCart() // Reload to update UI
            }
        }
    }
}