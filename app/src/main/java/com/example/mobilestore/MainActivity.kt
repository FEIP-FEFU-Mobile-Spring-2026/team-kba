package com.example.mobilestore

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilestore.adapter.ProductAdapter
import com.example.mobilestore.databinding.ActivityMainBinding
import com.example.mobilestore.model.Category
import com.example.mobilestore.viewmodel.CatalogUiState
import com.example.mobilestore.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var productAdapter: ProductAdapter
    private var isCatalogSelected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Сначала инициализируем ViewModel
        viewModel = MainViewModel(application)

        // Затем восстанавливаем состояние
        if (savedInstanceState != null) {
            isCatalogSelected = savedInstanceState.getBoolean("is_catalog_selected", true)
            val selectedCategory = savedInstanceState.getString("selected_category")
            selectedCategory?.let { viewModel.selectCategory(it) }
        }

        setupRecyclerView()
        setupBottomNavigation()
        observeViewModel()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selected_category", viewModel.selectedCategoryId.value)
        outState.putBoolean("is_catalog_selected", isCatalogSelected)
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList())
        productAdapter.setOnItemClickListener { product ->
            Toast.makeText(this, product.name, Toast.LENGTH_SHORT).show()
        }

        binding.productsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.productsRecyclerView.adapter = productAdapter
    }

    private fun setupBottomNavigation() {
        binding.menuButton.setOnClickListener {
            setBottomActive(true)
            isCatalogSelected = true
            // Здесь переход на каталог (он уже открыт)
        }

        binding.cartButton.setOnClickListener {
            setBottomActive(false)
            isCatalogSelected = false
            Toast.makeText(this, "Корзина в разработке", Toast.LENGTH_SHORT).show()
        }

        // Восстанавливаем состояние после поворота
        setBottomActive(isCatalogSelected)
    }

    private fun setBottomActive(isCatalogActive: Boolean) {
        if (isCatalogActive) {
            // Активный каталог
            binding.menuIcon.setColorFilter(getColor(R.color.bottom_nav_active))
            binding.menuText.setTextColor(getColor(R.color.bottom_nav_active))
            binding.cartIcon.setColorFilter(getColor(R.color.bottom_nav_inactive))
            binding.cartText.setTextColor(getColor(R.color.bottom_nav_inactive))
        } else {
            // Активная корзина
            binding.menuIcon.setColorFilter(getColor(R.color.bottom_nav_inactive))
            binding.menuText.setTextColor(getColor(R.color.bottom_nav_inactive))
            binding.cartIcon.setColorFilter(getColor(R.color.bottom_nav_active))
            binding.cartText.setTextColor(getColor(R.color.bottom_nav_active))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is CatalogUiState.Loading -> showLoading()
                    is CatalogUiState.Success -> showProducts(state)
                    is CatalogUiState.Error -> showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.productsRecyclerView.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showProducts(state: CatalogUiState.Success) {
        binding.progressBar.visibility = View.GONE
        binding.productsRecyclerView.visibility = View.VISIBLE
        binding.errorLayout.visibility = View.GONE

        productAdapter.updateProducts(state.products)
        setupCategories(state.categories, state.selectedCategoryId)
    }

    private fun setupCategories(categories: List<Category>, selectedId: String) {
        binding.categoriesContainer.removeAllViews()

        categories.forEach { category ->
            val categoryView = createCategoryView(category)
            binding.categoriesContainer.addView(categoryView)

            if (category.id == selectedId) {
                selectCategoryView(categoryView, true)  // Сразу делаем активной
            }

            categoryView.setOnClickListener {
                // Сброс всех
                for (i in 0 until binding.categoriesContainer.childCount) {
                    val child = binding.categoriesContainer.getChildAt(i)
                    selectCategoryView(child, false)
                }
                // Активация нажатой
                selectCategoryView(categoryView, true)
                selectCategory(category.id)
            }
        }
    }

    private fun createCategoryView(category: Category): TextView {
        val textView = TextView(this)
        textView.text = category.name
        textView.setPadding(36, 0, 36, 0)
        textView.setTextSize(14f)
        textView.gravity = Gravity.CENTER
        textView.maxLines = 1

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            100  // Высота 48dp (можно увеличить до 52, если нужно выше)
        )
        params.marginEnd = 18
        textView.layoutParams = params

        // Неактивный стиль по умолчанию (серый фон)
        textView.setBackgroundResource(R.drawable.bg_category_unselected)
        textView.setTextColor(getColor(R.color.black))

        return textView
    }

    private fun selectCategoryView(view: View, isSelected: Boolean) {
        if (!::binding.isInitialized) return  // Проверка, что binding инициализирован

        val textView = view as TextView
        if (isSelected) {
            textView.setBackgroundResource(R.drawable.bg_category_selected)
            textView.setTextColor(getColor(android.R.color.white))
        } else {
            textView.setBackgroundResource(R.drawable.bg_category_unselected)
            textView.setTextColor(getColor(R.color.black))
        }
    }

    private fun selectCategory(categoryId: String) {
        viewModel.selectCategory(categoryId)
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.productsRecyclerView.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE

        binding.retryButton.setOnClickListener {
            viewModel.retryLoad()
        }
    }
}