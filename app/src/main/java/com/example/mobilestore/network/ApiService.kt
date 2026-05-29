package com.example.mobilestore.network

import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("catalog")
    suspend fun getCatalog(
        @Header("Authorization") token: String
    ): ApiResponse  // ← изменили тип
}