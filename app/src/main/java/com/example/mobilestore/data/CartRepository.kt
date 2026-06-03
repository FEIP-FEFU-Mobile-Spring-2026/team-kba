package com.example.mobilestore.data

import android.content.Context
import com.example.mobilestore.model.CartItemEntity
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.Size
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class CartRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val cartDao = database.cartDao()
    private val productDao = database.productDao()

    fun getCartItems(): Flow<List<CartItem>> {
        return combine(
            cartDao.getAllItems(),
            productDao.getAllProducts()
        ) { cartItems, productEntities ->
            cartItems.mapNotNull { cartItem ->
                val productEntity = productEntities.find { it.id == cartItem.productId }
                if (productEntity != null) {
                    val product = productEntity.toProduct()
                    CartItem(
                        id = cartItem.id,
                        product = product,
                        size = product.sizes.find {
                            it.id == cartItem.sizeId
                        } ?: return@mapNotNull null,
                        quantity = cartItem.quantity
                    )
                } else {
                    null
                }
            }
        }
    }

    fun getCartCount(): Flow<Int> {
        return cartDao.getAllItems().map { it.size }
    }

    suspend fun addItem(product: Product, size: Size) {
        val existing = cartDao.getItem(product.id, size.id)
        if (existing != null) {
            cartDao.update(existing.copy(quantity = existing.quantity + 1))
        } else {
            cartDao.insert(CartItemEntity(productId = product.id, sizeId = size.id, quantity = 1))
        }
    }

    suspend fun removeItem(cartItem: CartItem) {
        cartDao.delete(
            CartItemEntity(
                id = cartItem.id,
                productId = cartItem.product.id,
                sizeId = cartItem.size.id,
                quantity = cartItem.quantity
            )
        )
    }

    suspend fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(cartItem)
        } else {
            cartDao.update(
                CartItemEntity(
                    id = cartItem.id,
                    productId = cartItem.product.id,
                    sizeId = cartItem.size.id,
                    quantity = newQuantity
                )
            )
        }
    }

    suspend fun clearCart() {
        cartDao.clearAll()
    }
}

data class CartItem(
    val id: Long,
    val product: Product,
    val size: Size,
    val quantity: Int
) {
    val totalPrice: Int = product.priceInKopecks * quantity
}

// Функция расширения для преобразования ProductEntity в Product
fun com.example.mobilestore.model.ProductEntity.toProduct(): Product {
    val gson = com.google.gson.Gson()
    return Product(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        priceInKopecks = priceInKopecks,
        imageUrl = imageUrl,
        tags = gson.fromJson(tags, Array<String>::class.java).toList(),
        categoryId = categoryId,
        sizes = gson.fromJson(sizes,
            Array<com.example.mobilestore.model.Size>::class.java)
            .toList(),
        material = material,
        weight = weight,
        season = season,
        countryOfOrigin = countryOfOrigin
    )
}
