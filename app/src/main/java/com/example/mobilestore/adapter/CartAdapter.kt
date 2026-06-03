package com.example.mobilestore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobilestore.R
import com.example.mobilestore.data.CartItem
import com.example.mobilestore.databinding.ItemCartBinding

class CartAdapter(
    private val onPlusClick: (CartItem) -> Unit,
    private val onMinusClick: (CartItem) -> Unit,
    private val onDeleteClick: (CartItem) -> Unit // ← Добавлено
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var items = listOf<CartItem>()

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(item: CartItem) {
            binding.cartTitle.text = item.product.name
            binding.cartSize.text = "Размер: ${item.size.name}"
            binding.cartPrice.text = "${item.totalPrice / 100} ₽"
            binding.countText.text = item.quantity.toString()

            Glide.with(binding.root.context)
                .load(item.product.imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(binding.cartImage)

            binding.btnPlus.setOnClickListener { onPlusClick(item) }
            binding.btnMinus.setOnClickListener { onMinusClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item) }
        }
    }
}
