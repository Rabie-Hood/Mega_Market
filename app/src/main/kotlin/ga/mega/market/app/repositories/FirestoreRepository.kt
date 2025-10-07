
package ga.mega.market.app.repositories

import com.google.firebase.firestore.FirebaseFirestore
import ga.mega.market.app.models.*
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val snapshot = db.collection("categories").get().await()
            val categories = snapshot.documents.mapNotNull { it.toObject(Category::class.java)?.copy(id = it.id) }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategory(categoryId: String): Result<List<Product>> {
        return try {
            val snapshot = db.collection("products").whereEqualTo("categoryId", categoryId).get().await()
            val products = snapshot.documents.mapNotNull { it.toObject(Product::class.java)?.copy(id = it.id) }
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProduct(productId: String): Result<Product> {
        return try {
            val doc = db.collection("products").document(productId).get().await()
            val product = doc.toObject(Product::class.java)?.copy(id = doc.id)
            if (product != null) Result.success(product) else Result.failure(Exception("Product not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCart(userId: String): Result<Map<String, CartItem>> {
        return try {
            val snapshot = db.collection("users").document(userId).collection("cart").get().await()
            val cart = snapshot.documents.associate { it.id to it.toObject(CartItem::class.java)!! }
            Result.success(cart)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(userId: String, productId: String, quantity: Int): Result<Unit> {
        return try {
            val cartItem = CartItem(quantity)
            db.collection("users").document(userId).collection("cart").document(productId).set(cartItem).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCartItem(userId: String, productId: String, quantity: Int): Result<Unit> {
        return try {
            if (quantity > 0) {
                val cartItem = CartItem(quantity)
                db.collection("users").document(userId).collection("cart").document(productId).set(cartItem).await()
            } else {
                db.collection("users").document(userId).collection("cart").document(productId).delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val docRef = db.collection("orders").add(order).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension to copy id
private fun Product.copy(id: String): Product = this.copy() // Assuming we add id to Product if needed, but for now, perhaps add id field later if needed.

Actually, data classes don't have id by default, but for simplicity, we can pass id separately or add id to models.

To match schema, documents have auto-generated IDs, so when fetching, we can include id.

But for now, since the task is to match schema, and schema uses auto-generated IDs, we can add id: String to the data classes.

Let's update the models to include id.

For User, Product, etc., add val id: String = "" or something.

But since it's auto-generated, when creating, don't set, when fetching, set.

For simplicity, add id to models.<apply_diff>
<path>app/src/main/kotlin/ga/mega/market/app/models/User.kt</path>
<diff>
<<<<<<< SEARCH
data class User(
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
=======
data class User(
    val id: String = "",
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val addresses: List<Address> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
>>>>>>> REPLACE
</diff>
</apply_diff>