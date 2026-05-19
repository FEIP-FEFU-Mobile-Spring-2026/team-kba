package com.example.mobilestore

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        Toast.makeText(this, "Корзина в разработке", Toast.LENGTH_LONG).show()
    }
}