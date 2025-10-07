package ga.mega.market.app.models

import com.google.firebase.Timestamp

data class User(
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)