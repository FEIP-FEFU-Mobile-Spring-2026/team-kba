package com.example.mobilestore

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilestore.adapter.ProductAdapter
import com.example.mobilestore.model.Product
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var btnNew: MaterialButton
    private lateinit var btnJeans: MaterialButton
    private lateinit var btnTshirts: MaterialButton
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar)
        btnNew = findViewById(R.id.btnNew)
        btnJeans = findViewById(R.id.btnJeans)
        btnTshirts = findViewById(R.id.btnTshirts)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        bottomNav = findViewById(R.id.bottomNav)

        setupToolbar()
        setupCategoryButtons()
        setupRecyclerView()
        setupBottomNavigation()
        loadProducts()

        selectButton(btnNew)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    private fun setupCategoryButtons() {
        btnNew.setOnClickListener {
            selectButton(btnNew)
            filterProducts("new")
        }

        btnJeans.setOnClickListener {
            selectButton(btnJeans)
            filterProducts("jeans")
        }

        btnTshirts.setOnClickListener {
            selectButton(btnTshirts)
            filterProducts("tshirts")
        }
    }

    private fun selectButton(button: MaterialButton) {
        resetAllButtons()
        button.setTextColor(getColor(R.color.text_on_accent))
        button.strokeColor = getColorStateList(R.color.chip_selected_stroke)
        button.backgroundTintList = getColorStateList(R.color.chip_selected_background)
    }

    private fun resetAllButtons() {
        btnNew.setTextColor(getColor(R.color.text_primary))
        btnNew.strokeColor = getColorStateList(R.color.chip_stroke)
        btnNew.backgroundTintList = getColorStateList(R.color.chip_background)

        btnJeans.setTextColor(getColor(R.color.text_primary))
        btnJeans.strokeColor = getColorStateList(R.color.chip_stroke)
        btnJeans.backgroundTintList = getColorStateList(R.color.chip_background)

        btnTshirts.setTextColor(getColor(R.color.text_primary))
        btnTshirts.strokeColor = getColorStateList(R.color.chip_stroke)
        btnTshirts.backgroundTintList = getColorStateList(R.color.chip_background)
    }

    private fun filterProducts(category: String) {
        loadProducts()
    }

    private fun setupRecyclerView() {
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    private fun setupBottomNavigation() {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )

        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.accent),
            ContextCompat.getColor(this, R.color.bottom_nav_color)
        )

        val colorStateList = ColorStateList(states, colors)

        bottomNav.itemIconTintList = colorStateList
        bottomNav.itemTextColor = colorStateList

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item -> {
                    Toast.makeText(this, "Меню нажато", Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.cart_item -> {
                    Toast.makeText(this, "Корзина нажата", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }

    private fun loadProducts() {
        val mockProducts = getMockProducts()
        productAdapter = ProductAdapter(mockProducts, this)
        productsRecyclerView.adapter = productAdapter
    }

    private fun getMockProducts(): List<Product> {
        return listOf(
            Product(
                id = 1,
                title = "Джинсовая куртка",
                price = 3599.0,
                category = "jackets",
                description = "Стильная джинсовая куртка бежевого цвета.",
                fullText = "Удобная и универсальная джинсовая куртка, подходящая для повседневного образа. Легко сочетается с брюками, джинсами или юбками.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/c8157c9177355ec270e33d66c49ef450.png"
            ),
            Product(
                id = 2,
                title = "Толстовка с капюшоном",
                price = 3499.0,
                category = "hoodies",
                description = "Розовая толстовка с капюшоном.",
                fullText = "Комфортная толстовка с капюшоном, создающая уютный и яркий повседневный образ. Отлично подходит для прогулок и активного отдыха.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/e9d3dbd70fe147f3f73e130dd3d6fa46.png"
            ),
            Product(
                id = 3,
                title = "Рубашка поло",
                price = 1699.0,
                category = "shirts",
                description = "Темно-синяя рубашка поло.",
                fullText = "Элегантная рубашка поло с классическим кроем. Подходит как для повседневного, так и для более официального образа.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/726a93ff5ecd6f574ec1c844f47edb53.png"
            ),
            Product(
                id = 4,
                title = "Базовые спортивные брюки",
                price = 2999.0,
                category = "pants",
                description = "Черные спортивные брюки для повседневной носки.",
                fullText = "Удобные спортивные брюки с зауженным кроем. Отличный выбор для активного отдыха, тренировок и прогулок.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/23636f36fc406e3f5a9a22facf6faf13.png"
            ),
            Product(
                id = 5,
                title = "Базовые зауженные джинсы",
                price = 3499.0,
                category = "jeans",
                description = "Синие зауженные джинсы.",
                fullText = "Классические джинсы с зауженным кроем, подходящие для создания современных повседневных образов. Легко комбинируются с различными верхами.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/48f57404bdcb22b22e859b5fdf6992d8.png"
            ),
            Product(
                id = 6,
                title = "Трикотажные шорты",
                price = 1899.0,
                category = "shorts",
                description = "Бежевые трикотажные шорты.",
                fullText = "Удобные шорты для лета и повседневной носки. Легкие и практичные, отлично подходят для прогулок и отдыха на свежем воздухе.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/f444639ba5448d19ac7f085fcbb50610.png"
            ),
            Product(
                id = 7,
                title = "Низкие сникерсы",
                price = 2399.0,
                category = "shoes",
                description = "Белые низкие сникерсы.",
                fullText = "Классические белые сникерсы для повседневной носки. Легко сочетаются с джинсами, шортами и спортивными костюмами.",
                image = "https://nyerblobstoreprod.blob.core.windows.net/product-images-public/84d21d80391a53a3e71d91f524857234.png"
            )
        )
    }
}