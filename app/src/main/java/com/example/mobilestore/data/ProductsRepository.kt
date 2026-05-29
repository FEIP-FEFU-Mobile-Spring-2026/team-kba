package com.example.mobilestore.data

import android.content.Context
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ProductsRepository(private val context: Context) {

    fun loadProducts(): Flow<Result<List<Product>>> = flow {
        emit(Result.Loading)

        try {
            val products = withContext(Dispatchers.IO) {
                loadProductsFromJson()
            }
            emit(Result.Success(products))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    private fun loadProductsFromJson(): List<Product> {
        val jsonString = readJsonFromAssets()
        val jsonObject = JSONObject(jsonString)
        val itemsArray = jsonObject.getJSONArray("items")

        val products = mutableListOf<Product>()

        for (i in 0 until itemsArray.length()) {
            val item = itemsArray.getJSONObject(i)

            // Читаем теги
            val tags = mutableListOf<String>()
            val tagsArray = item.optJSONArray("tags")
            if (tagsArray != null) {
                for (j in 0 until tagsArray.length()) {
                    tags.add(tagsArray.getString(j))
                }
            }

            // Читаем размеры
            val sizes = mutableListOf<Size>()
            val sizesArray = item.optJSONArray("sizes")
            if (sizesArray != null) {
                for (j in 0 until sizesArray.length()) {
                    val sizeObj = sizesArray.getJSONObject(j)
                    sizes.add(Size(
                        id = sizeObj.getString("id"),
                        name = sizeObj.getString("name")
                    ))
                }
            }

            products.add(
                Product(
                    id = item.getString("id"),
                    name = item.getString("name"),
                    shortDescription = item.getString("shortDescription"),
                    longDescription = item.getString("longDescription"),
                    priceInKopecks = item.getInt("priceInKopecks"),
                    imageUrl = item.getString("imageUrl"),
                    tags = tags,
                    categoryId = item.getString("categoryId"),
                    sizes = sizes,
                    material = item.optString("material", ""),
                    weight = item.optString("weight", ""),
                    season = item.optString("season", ""),
                    countryOfOrigin = item.optString("countryOfOrigin", "")
                )
            )
        }

        return products
    }

    private fun readJsonFromAssets(): String {
        return context.assets.open("products.json")
            .bufferedReader()
            .use { it.readText() }
    }
}

sealed class Result<out T> {
    object Loading : Result<Nothing>()
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}