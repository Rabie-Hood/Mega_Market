package ga.mega.market.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import ga.mega.market.app.models.Category
import ga.mega.market.app.models.Product
import ga.mega.market.app.viewmodels.ProductCatalogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCatalogScreen(onProductClick: (Product) -> Unit, viewModel: ProductCatalogViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Product Catalog") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else {
                LazyRow(modifier = Modifier.padding(8.dp)) {
                    items(categories) { category ->
                        CategoryItem(category = category, isSelected = category.id == selectedCategoryId) {
                            viewModel.selectCategory(category.id)
                        }
                    }
                }

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(products) { product ->
                        ProductItem(product = product) {
                            onProductClick(product)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = category.image,
                contentDescription = category.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Text(category.name, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ProductItem(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.images.firstOrNull(),
                contentDescription = product.name,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text("$${product.price}", style = MaterialTheme.typography.bodyMedium)
                Text(product.description, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            }
        }
    }
}