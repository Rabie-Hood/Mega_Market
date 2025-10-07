package ga.mega.market.app.models

data class OrderItem(
    val productId: String,
    val name: String,
    val price: Double,
    val quantity: Int
)