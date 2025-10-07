# Mega Market Database Schema

This document describes the database schema for the Mega Market application, based on Firebase Firestore. The schema is designed to store product information, photos, user data, orders, and shopping cart details. Data is organized into collections and documents for flexibility and scalability.

## Overview

The database uses Firebase Firestore, a NoSQL document database. Collections represent groups of documents, and each document contains fields with data. Relationships are handled through references (e.g., document IDs).

## Collections

### Users Collection

Stores user account information and profile data.

- **Document ID**: Auto-generated unique identifier from Firebase Auth.
- **Fields**:
  - `email` (String): The user's email address, used for authentication and communication.
  - `displayName` (String): The user's full name for display purposes.
  - `phoneNumber` (String, optional): The user's phone number for contact and delivery purposes.
  - `addresses` (Array of Objects): List of delivery addresses. Each address object contains:
    - `street` (String): Street address.
    - `city` (String): City name.
    - `state` (String): State or province.
    - `zipCode` (String): Postal code.
    - `country` (String): Country name.
  - `createdAt` (Timestamp): Date and time when the user account was created.
  - `updatedAt` (Timestamp): Date and time when the user profile was last updated.

### Products Collection

Stores information about products available in the market.

- **Document ID**: Auto-generated unique identifier for each product.
- **Fields**:
  - `name` (String): The name of the product.
  - `description` (String): A detailed description of the product, including specifications and features.
  - `price` (Number): The current selling price of the product.
  - `originalPrice` (Number, optional): The original price before any discounts; used to show savings.
  - `images` (Array of Strings): URLs to product images stored in Firebase Cloud Storage.
  - `categoryId` (String): Reference to the document ID of the product's category in the Categories collection.
  - `brand` (String): The brand or manufacturer of the product.
  - `stockQuantity` (Number): The number of items available in stock.
  - `isActive` (Boolean): Indicates if the product is currently available for purchase (true) or discontinued (false).
  - `tags` (Array of Strings): Keywords or tags for search functionality and filtering.
  - `createdAt` (Timestamp): Date and time when the product was added to the database.
  - `updatedAt` (Timestamp): Date and time when the product information was last updated.

### Categories Collection

Stores category information for organizing products.

- **Document ID**: Auto-generated unique identifier for each category.
- **Fields**:
  - `name` (String): The name of the category (e.g., "Groceries", "Electronics").
  - `description` (String, optional): A brief description of the category.
  - `parentId` (String, optional): Reference to the parent category document ID for hierarchical structure (subcategories).
  - `image` (String): URL to the category image stored in Firebase Cloud Storage.
  - `isActive` (Boolean): Indicates if the category is active and visible to users.
  - `createdAt` (Timestamp): Date and time when the category was created.

### Orders Collection

Stores order information for completed purchases.

- **Document ID**: Auto-generated unique identifier for each order.
- **Fields**:
  - `userId` (String): Reference to the document ID of the user who placed the order in the Users collection.
  - `items` (Array of Objects): List of items in the order. Each item object contains:
    - `productId` (String): Reference to the product document ID.
    - `name` (String): Product name at the time of order.
    - `price` (Number): Price per unit at the time of order.
    - `quantity` (Number): Number of units ordered.
  - `subtotal` (Number): Total cost of items before taxes and shipping.
  - `tax` (Number): Tax amount applied to the order.
  - `shippingCost` (Number): Cost of shipping or delivery.
  - `total` (Number): Final total amount including subtotal, tax, and shipping.
  - `status` (String): Current status of the order (e.g., "pending", "confirmed", "processing", "shipped", "delivered", "cancelled").
  - `shippingAddress` (Object): The selected delivery address, matching the structure in the user's addresses array.
  - `paymentMethod` (String): Description of the payment method used (e.g., "Credit Card", "PayPal").
  - `paymentStatus` (String): Status of the payment (e.g., "pending", "completed", "failed").
  - `createdAt` (Timestamp): Date and time when the order was placed.
  - `updatedAt` (Timestamp): Date and time when the order was last updated.

### Carts Subcollection (under Users)

Stores shopping cart items for each user. This is a subcollection under each user document.

- **Document ID**: The product ID from the Products collection.
- **Fields**:
  - `quantity` (Number): The number of units of the product in the cart.
  - `addedAt` (Timestamp): Date and time when the item was added to the cart.

## Additional Notes

- **Security**: Firestore security rules should be implemented to control read/write access based on user authentication and authorization.
- **Validation**: Data validation should be enforced at the application level to ensure data integrity.
- **Indexing**: Create indexes for frequently queried fields such as `categoryId`, `userId`, and `status` to optimize query performance.
- **Relationships**: References between documents are handled using document IDs. For example, `categoryId` in Products references a document in Categories.
- **Real-time Updates**: Firestore supports real-time listeners for automatic UI updates when data changes.
- **Storage**: Product and category images are stored in Firebase Cloud Storage, with URLs referenced in the database.