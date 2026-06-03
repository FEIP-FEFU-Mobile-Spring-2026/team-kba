package com.example.mobilestore.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mobilestore.ProductDetailBottomSheet
import com.example.mobilestore.R
import com.example.mobilestore.databinding.ItemProductBinding
import com.example.mobilestore.model.Product

class ProductAdapter(
    private var products: List<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var onItemClick: ((Product) -> Unit)? = null
    var onQuantityChange: ((Product, Int) -> Unit)? = null
    var getQuantity: ((String) -> Int)? = null

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, getQuantity?.invoke(product.id) ?: 0)

        holder.itemView.setOnClickListener {
            val bottomSheet = ProductDetailBottomSheet()
            val args = Bundle()
            args.putParcelable("PRODUCT", product)
            bottomSheet.arguments = args
            bottomSheet.show(
                (holder.itemView.context as androidx.appcompat.app.AppCompatActivity)
                    .supportFragmentManager,
                "ProductDetailBottomSheet"
            )
        }
    }

    override fun getItemCount(): Int = products.size

    inner class ProductViewHolder(
        private val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, quantity: Int) {
            binding.productTitle.text = product.name
            binding.productDescription.text = product.shortDescription

            val priceInRubles = product.priceInKopecks / 100.0
            val formattedPrice = String.format("%,d ₽", priceInRubles.toInt())
                .replace(",", " ")
            binding.productPrice.text = formattedPrice

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                )
                .into(binding.productImage)

            if (quantity > 0) {
                binding.productPrice.visibility = View.GONE
                binding.counterLayout.visibility = View.VISIBLE
                binding.counterText.text = quantity.toString()
            } else {
                binding.productPrice.visibility = View.VISIBLE
                binding.counterLayout.visibility = View.GONE
            }

            binding.btnPlus.setOnClickListener {
                onQuantityChange?.invoke(product, 1)
            }
            binding.btnMinus.setOnClickListener {
                if (quantity > 1) {
                    onQuantityChange?.invoke(product, -1)
                } else {
                    onQuantityChange?.invoke(product, -1)
                }
            }
        }
    }
}
