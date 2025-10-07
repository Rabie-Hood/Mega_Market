package ga.mega.market.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ga.mega.market.app.models.Product
import ga.mega.market.app.repositories.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductDetailsViewModel(private val firestoreRepository: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _addToCartResult = MutableStateFlow<Result<Unit>?>(null)
    val addToCartResult: StateFlow<Result<Unit>?> = _addToCartResult

    fun loadProduct(productId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firestoreRepository.getProduct(productId)
            if (result.isSuccess) {
                _product.value = result.getOrNull()
            }
            _isLoading.value = false
        }
    }

    fun addToCart(quantity: Int = 1) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val prod = _product.value ?: return
        viewModelScope.launch {
            val result = firestoreRepository.addToCart(userId, prod.id, quantity)
            _addToCartResult.value = result
        }
    }
}