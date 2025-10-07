package ga.mega.market.app.models

import com.google.firebase.Timestamp

data class Category(
    val id: String = "",
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val image: String,
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now()
)