package com.example.mobilestore.network

import com.example.mobilestore.model.Category
import com.example.mobilestore.model.Product

data class ApiResponse(
    val categories: List<Category>,
    val items: List<Product>
)