package com.example.mobilestore.viewmodel

import com.example.mobilestore.model.Product
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ProductFilterTest {

    private val products = listOf(
        createProduct("1", "cat_jeans", emptyList()),
        createProduct("2", "cat_tshirts", emptyList()),
        createProduct("3", "cat_jeans", listOf("New")),
        createProduct("4", "cat_outerwear", listOf("New"))
    )

    @Test
    fun `filterByNew should return products with New tag`() {
        val result = products.filter { it.tags.contains("New") }
        assertEquals(2, result.size)
        assertEquals("3", result[0].id)
        assertEquals("4", result[1].id)
    }

    @Test
    fun `filterByCategory should return products with matching categoryId`() {
        val result = products.filter { it.categoryId == "cat_jeans" }
        assertEquals(2, result.size)
    }

    @Test
    fun `filterByNew should return empty list if no New tags`() {
        val productsWithoutNew = listOf(
            createProduct("1", "cat_jeans", emptyList()),
            createProduct("2", "cat_tshirts", emptyList())
        )
        val result = productsWithoutNew.filter { it.tags.contains("New") }
        assertEquals(0, result.size)
    }

    @Test
    fun `extractUniqueCategories should return distinct category ids`() {
        val uniqueIds = products.map { it.categoryId }.distinct()
        assertEquals(3, uniqueIds.size)
        assertEquals("cat_jeans", uniqueIds[0])
        assertEquals("cat_tshirts", uniqueIds[1])
        assertEquals("cat_outerwear", uniqueIds[2])
    }

    private fun createProduct(id: String, categoryId: String, tags: List<String>): Product {
        return Product(
            id = id, name = "Product $id", shortDescription = "", longDescription = "",
            priceInKopecks = 1000, imageUrl = "", tags = tags, categoryId = categoryId,
            sizes = emptyList(), material = "", weight = "", season = "", countryOfOrigin = ""
        )
    }
}
