package com.example.mobilestore

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobilestore.adapter.CartAdapter
import com.example.mobilestore.data.CartRepository
import com.example.mobilestore.databinding.ActivityCartBinding
import com.example.mobilestore.viewmodel.CartViewModel
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupFormValidation()
        setupButtons()
        setupBottomNavigation()
        observeCartCount()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(
            onPlusClick = { cartItem ->
                viewModel.updateQuantity(cartItem, cartItem.quantity + 1)
            },
            onMinusClick = { cartItem ->
                if (cartItem.quantity > 1) {
                    viewModel.updateQuantity(cartItem, cartItem.quantity - 1)
                }
            }
        )
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.cartRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.cartItems)
                binding.totalPriceText.text = "${state.totalPrice / 100} ₽"

                if (state.cartItems.isEmpty()) {
                    binding.cartRecyclerView.visibility = View.GONE
                    binding.emptyCartLayout.visibility = View.VISIBLE
                    binding.cartBottom.visibility = View.GONE
                } else {
                    binding.cartRecyclerView.visibility = View.VISIBLE
                    binding.emptyCartLayout.visibility = View.GONE
                    binding.cartBottom.visibility = View.VISIBLE
                }

                val isFormValid = viewModel.validateOrder(
                    state.name,
                    state.email
                )
                binding.checkoutButton.isEnabled = isFormValid && state.cartItems.isNotEmpty()
            }
        }
    }

    private fun setupFormValidation() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = binding.nameInput.text?.toString() ?: ""
                val email = binding.emailInput.text?.toString() ?: ""
                viewModel.updateFormData(name, email)
            }
        }

        binding.nameInput.addTextChangedListener(textWatcher)
        binding.emailInput.addTextChangedListener(textWatcher)
    }

    private fun setupButtons() {
        binding.clearCartBtn.setOnClickListener {
            if (viewModel.uiState.value.cartItems.isNotEmpty()) {
                showClearCartDialog()
            }
        }

        binding.checkoutButton.setOnClickListener {
            val name = binding.nameInput.text?.toString() ?: ""
            val email = binding.emailInput.text?.toString() ?: ""
            val comment = binding.commentInput.text?.toString() ?: ""

            if (viewModel.validateOrder(name, email)) {
                showSuccessDialog()
                viewModel.placeOrder(name, email, comment)
            } else {
                Toast.makeText(this, "Заполните все поля корректно", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupBottomNavigation() {
        binding.menuButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showClearCartDialog() {
        AlertDialog.Builder(this)
            .setTitle("Очистка корзины")
            .setMessage("Вы уверены, что хотите удалить все товары из корзины?")
            .setPositiveButton("Очистить") { _, _ ->
                viewModel.clearCart()
                Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    private fun observeCartCount() {
        lifecycleScope.launch {
            val repository = com.example.mobilestore.data.CartRepository(application)
            repository.getCartCount().collect { count ->
                binding.cartCountText.apply {
                    if (count > 0) {
                        text = count.toString()
                        visibility = View.VISIBLE
                    } else {
                        visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Заказ оформлен!")
            .setMessage("Подтверждение и чек отправили на вашу почту")
            .setPositiveButton("Вернуться в каталог") { _, _ ->
                finish()
            }
            .show()
    }
}