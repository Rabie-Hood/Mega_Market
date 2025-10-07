package ga.mega.market.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ga.mega.market.app.models.Category
import ga.mega.market.app.models.Product
import ga.mega.market.app.repositories.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductCatalogViewModel(private val firestoreRepository: FirestoreRepository = FirestoreRepository()) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firestoreRepository.getCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrNull() ?: emptyList()
            }
            _isLoading.value = false
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        loadProducts(categoryId)
    }

    private fun loadProducts(categoryId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = firestoreRepository.getProductsByCategory(categoryId)
            if (result.isSuccess) {
                _products.value = result.getOrNull() ?: emptyList()
            }
            _isLoading.value = false
        }
    }
}