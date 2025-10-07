package ga.mega.market.app.models

import com.google.firebase.Timestamp

data class CartItem(
    val quantity: Int,
    val addedAt: Timestamp = Timestamp.now()
)