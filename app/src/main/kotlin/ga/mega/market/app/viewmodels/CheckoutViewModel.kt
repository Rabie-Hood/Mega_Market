package ga.mega.market.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ga.mega.market.app.models.Address
import ga.mega.market.app.models.Order
import ga.mega.market.app.models.OrderItem
import ga.mega.market.app.repositories.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val cartViewModel: CartViewModel = CartViewModel(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
) : ViewModel() {

    val cartWithProducts = cartViewModel.cartWithProducts
    val total = cartViewModel.total

    private val _shippingAddress = MutableStateFlow(Address("", "", "", "", ""))
    val shippingAddress: StateFlow<Address> = _shippingAddress

    private val _paymentMethod = MutableStateFlow("")
    val paymentMethod: StateFlow<String> = _paymentMethod

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder

    private val _orderResult = MutableStateFlow<Result<String>?>(null)
    val orderResult: StateFlow<Result<String>?> = _orderResult

    fun updateShippingAddress(address: Address) {
        _shippingAddress.value = address
    }

    fun updatePaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun placeOrder() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val items = cartWithProducts.value.map { (product, cartItem) ->
            OrderItem(product.id, product.name, product.price, cartItem.quantity)
        }
        val subtotal = total.value
        val tax = subtotal * 0.1 // 10% tax
        val shippingCost = 5.0 // fixed
        val totalAmount = subtotal + tax + shippingCost
        val order = Order(
            userId = userId,
            items = items,
            subtotal = subtotal,
            tax = tax,
            shippingCost = shippingCost,
            total = totalAmount,
            status = "pending",
            shippingAddress = shippingAddress.value,
            paymentMethod = paymentMethod.value,
            paymentStatus = "pending"
        )

        _isPlacingOrder.value = true
        viewModelScope.launch {
            val result = firestoreRepository.placeOrder(order)
            _orderResult.value = result
            _isPlacingOrder.value = false
        }
    }
}