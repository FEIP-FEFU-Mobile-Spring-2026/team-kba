package com.example.mobilestore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilestore.data.CartItem
import com.example.mobilestore.data.CartRepository
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.Size
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository(application)

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCartItems()
    }

    private fun loadCartItems() {
        viewModelScope.launch {
            repository.getCartItems().collect { items ->
                _uiState.update { state ->
                    state.copy(
                        cartItems = items,
                        totalPrice = items.sumOf { it.totalPrice },
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getCartCount().collect { count ->
                _uiState.update { it.copy(cartCount = count) }
            }
        }
    }

    fun addToCart(product: Product, size: Size) {
        viewModelScope.launch {
            repository.addItem(product, size)
        }
    }

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(cartItem, newQuantity)
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            repository.removeItem(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Для оформления заказа
    fun validateOrder(name: String, email: String): Boolean {
        return name.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun placeOrder(name: String, email: String, comment: String) {
        viewModelScope.launch {
            // Здесь можно отправить заказ на сервер
            // Пока просто очищаем корзину
            repository.clearCart()
        }
    }
    fun updateFormData(name: String, email: String) {
        _uiState.update { state ->
            state.copy(
                name = name,
                email = email
            )
        }
    }
}

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Int = 0,
    val cartCount: Int = 0,
    val isLoading: Boolean = true,
    val name: String = "",
    val email: String = "",
    val comment: String = "",
    val isNameValid: Boolean = true,
    val isEmailValid: Boolean = true
)