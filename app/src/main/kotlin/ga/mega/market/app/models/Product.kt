package ga.mega.market.app.models

import com.google.firebase.Timestamp

data class Product(
    val id: String = "",
    val name: String,
    val description: String,
    val price: Double,
    val originalPrice: Double? = null,
    val images: List<String> = emptyList(),
    val categoryId: String,
    val brand: String,
    val stockQuantity: Int,
    val isActive: Boolean = true,
    val tags: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)