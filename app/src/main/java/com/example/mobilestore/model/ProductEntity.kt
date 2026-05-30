package com.example.mobilestore.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: String, // JSON
    val categoryId: String,
    val sizes: String, // JSON
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String
)