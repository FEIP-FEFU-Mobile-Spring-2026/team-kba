package com.example.mobilestore.data

import android.content.Context
import com.example.mobilestore.model.CartItemEntity
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.Size
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CartRepositoryTest {

    private lateinit var mockContext: Context
    private lateinit var mockCartDao: CartDao
    private lateinit var mockProductDao: ProductDao
    private lateinit var mockDatabase: AppDatabase
    private lateinit var repository: CartRepository

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockCartDao = mockk()
        mockProductDao = mockk()
        mockDatabase = mockk()

        mockkStatic(AppDatabase::class)
        every { AppDatabase.getInstance(mockContext) } returns mockDatabase
        every { mockDatabase.cartDao() } returns mockCartDao
        every { mockDatabase.productDao() } returns mockProductDao

        repository = CartRepository(mockContext)
    }

    @Test
    fun `addItem should increase quantity if item exists`() = runBlocking {
        val existingItem = CartItemEntity(id = 1L, productId = "p1", sizeId = "s1", quantity = 2)
        coEvery { mockCartDao.getItem("p1", "s1") } returns existingItem
        coEvery { mockCartDao.update(any()) } just Runs

        val product = mockk<Product>()
        val size = mockk<Size>()
        every { product.id } returns "p1"
        every { size.id } returns "s1"

        repository.addItem(product, size)

        coVerify { mockCartDao.update(existingItem.copy(quantity = 3)) }
    }

    @Test
    fun `addItem should insert new item if not exists`() = runBlocking {
        coEvery { mockCartDao.getItem("p1", "s1") } returns null
        coEvery { mockCartDao.insert(any()) } just Runs

        val product = mockk<Product>()
        val size = mockk<Size>()
        every { product.id } returns "p1"
        every { size.id } returns "s1"

        repository.addItem(product, size)

        coVerify { mockCartDao.insert(any()) }
    }

    @Test
    fun `getCartCount should return number of items`() = runBlocking {
        val items = listOf(
            CartItemEntity(1L, "p1", "s1", 2),
            CartItemEntity(2L, "p2", "s2", 1)
        )
        coEvery { mockCartDao.getAllItems() } returns flowOf(items)

        val result = repository.getCartCount().first()
        assertEquals(2, result)
    }

    @Test
    fun `clearCart should delete all items`() = runBlocking {
        coEvery { mockCartDao.clearAll() } just Runs

        repository.clearCart()

        coVerify { mockCartDao.clearAll() }
    }
}
