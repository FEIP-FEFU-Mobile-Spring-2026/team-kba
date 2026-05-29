package com.example.mobilestore.data

import android.content.Context
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.ProductEntity
import com.example.mobilestore.model.Size
import com.example.mobilestore.network.ApiResponse
import com.example.mobilestore.network.ApiService
import com.example.mobilestore.network.NetworkHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductsRepository(private val context: Context) {

    private val database = AppDatabase.getInstance(context)
    private val productDao = database.productDao()
    private val networkHelper = NetworkHelper(context)
    private val gson = Gson()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://fefu2026spring.deploy.feip.dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val TOKEN = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"

    fun getProductsStream(): Flow<List<Product>> {
        return productDao.getAllProducts().map { entities ->
            entities.map { it.toProduct() }
        }
    }

    suspend fun refreshFromApi(): RefreshResult {
        if (!networkHelper.isNetworkAvailable()) {
            return RefreshResult.NoNetwork
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCatalog(TOKEN)
                saveToCache(response.items)
                RefreshResult.Success
            } catch (e: Exception) {
                RefreshResult.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun saveToCache(products: List<Product>) {
        val entities = products.map { product ->
            ProductEntity(
                id = product.id,
                name = product.name,
                shortDescription = product.shortDescription,
                longDescription = product.longDescription,
                priceInKopecks = product.priceInKopecks,
                imageUrl = product.imageUrl,
                tags = gson.toJson(product.tags),
                categoryId = product.categoryId,
                sizes = gson.toJson(product.sizes),
                material = product.material,
                weight = product.weight,
                season = product.season,
                countryOfOrigin = product.countryOfOrigin
            )
        }
        productDao.clearAll()
        productDao.insertAll(entities)
    }

    fun loadProducts(): Flow<LoadResult> = flow {
        val cached = productDao.getAllProducts().first()

        if (cached.isNotEmpty()) {
            emit(LoadResult.Cache(cached.map { it.toProduct() }))
        }

        when (val result = refreshFromApi()) {
            is RefreshResult.Success -> {
                val fresh = productDao.getAllProducts().first()
                emit(LoadResult.Success(fresh.map { it.toProduct() }))
            }
            is RefreshResult.NoNetwork -> {
                if (cached.isEmpty()) {
                    emit(LoadResult.Error("Нет сети и нет закэшированных данных"))
                } else {
                    emit(LoadResult.NoNetworkButHasCache)
                }
            }
            is RefreshResult.Error -> {
                if (cached.isEmpty()) {
                    emit(LoadResult.Error(result.message))
                } else {
                    emit(LoadResult.CacheWithError(cached.map { it.toProduct() }, result.message))
                }
            }
        }
    }

    private fun ProductEntity.toProduct(): Product {
        return Product(
            id = id,
            name = name,
            shortDescription = shortDescription,
            longDescription = longDescription,
            priceInKopecks = priceInKopecks,
            imageUrl = imageUrl,
            tags = gson.fromJson(tags, Array<String>::class.java).toList(),
            categoryId = categoryId,
            sizes = gson.fromJson(sizes, Array<Size>::class.java).toList(),
            material = material,
            weight = weight,
            season = season,
            countryOfOrigin = countryOfOrigin
        )
    }
}

sealed class RefreshResult {
    object Success : RefreshResult()
    object NoNetwork : RefreshResult()
    data class Error(val message: String) : RefreshResult()
}

sealed class LoadResult {
    data class Cache(val products: List<Product>) : LoadResult()
    data class Success(val products: List<Product>) : LoadResult()
    data class Error(val message: String) : LoadResult()
    object NoNetworkButHasCache : LoadResult()
    data class CacheWithError(val products: List<Product>, val message: String) : LoadResult()
}