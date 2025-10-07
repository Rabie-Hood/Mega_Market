package ga.mega.market.app.models

import com.google.firebase.Timestamp

data class Order(
    val id: String = "",
    val userId: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val tax: Double,
    val shippingCost: Double,
    val total: Double,
    val status: String,
    val shippingAddress: Address,
    val paymentMethod: String,
    val paymentStatus: String,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)