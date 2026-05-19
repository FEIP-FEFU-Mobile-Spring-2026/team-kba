package com.example.mobilestore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilestore.data.ProductsRepository
import com.example.mobilestore.data.Result
import com.example.mobilestore.model.Category
import com.example.mobilestore.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductsRepository(application)

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<String>("new")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private var allProducts = listOf<Product>()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.loadProducts().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = CatalogUiState.Loading
                    }
                    is Result.Success -> {
                        allProducts = result.data

                        val categories = extractCategories(result.data)
                        val categoriesWithNews = listOf(
                            Category("new", "Новинки")
                        ) + categories

                        _uiState.value = CatalogUiState.Success(
                            products = filterProductsByCategory(_selectedCategoryId.value),
                            categories = categoriesWithNews,
                            selectedCategoryId = _selectedCategoryId.value
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = CatalogUiState.Error(result.message)
                    }
                }
            }
        }
    }

    private fun extractCategories(products: List<Product>): List<Category> {
        val uniqueCategories = products.map { it.categoryId }.distinct()
        val categoryNames = mapOf(
            "cat_jeans" to "Джинсы",
            "cat_tshirts" to "Футболки",
            "cat_shirts" to "Рубашки",
            "cat_shoes" to "Обувь",
            "cat_outerwear" to "Верхняя одежда"
        )

        return uniqueCategories.map { categoryId ->
            Category(categoryId, categoryNames[categoryId] ?: categoryId)
        }
    }

    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId

        if (_uiState.value is CatalogUiState.Success) {
            val currentState = _uiState.value as CatalogUiState.Success
            _uiState.value = currentState.copy(
                products = filterProductsByCategory(categoryId),
                selectedCategoryId = categoryId
            )
        }
    }

    private fun filterProductsByCategory(categoryId: String): List<Product> {
        return when (categoryId) {
            "new" -> allProducts.filter { it.tags.contains("New") }
            else -> allProducts.filter { it.categoryId == categoryId }
        }
    }

    fun retryLoad() {
        loadProducts()
    }
}

sealed class CatalogUiState {
    object Loading : CatalogUiState()
    data class Success(
        val products: List<Product>,
        val categories: List<Category>,
        val selectedCategoryId: String
    ) : CatalogUiState()
    data class Error(val message: String) : CatalogUiState()
}