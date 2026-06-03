package com.example.mobilestore.data

import androidx.room.*
import com.example.mobilestore.model.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Update
    suspend fun update(item: CartItemEntity)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clearAll()

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND sizeId = :sizeId")
    suspend fun getItem(productId: String, sizeId: String): CartItemEntity?
}