package com.example.mobilestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mobilestore.ProductDetailBottomSheet
import com.example.mobilestore.R
import com.example.mobilestore.model.Product

class ProductAdapter(
    private val products: List<Product>,
    private val context: android.content.Context
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            val bottomSheet = ProductDetailBottomSheet()

            val args = android.os.Bundle()
            args.putParcelable("PRODUCT", product)
            bottomSheet.arguments = args

            bottomSheet.show(
                (context as androidx.appcompat.app.AppCompatActivity).supportFragmentManager,
                "ProductDetailBottomSheet"
            )
        }
    }

    override fun getItemCount() = products.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productTitle: TextView = itemView.findViewById(R.id.productTitle)
        private val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)

        fun bind(product: Product) {
            productTitle.text = product.title
            productDescription.text = product.description

            val priceFormatted = String.format("%,d ₽", product.price.toLong()).replace(",", " ")
            productPrice.text = priceFormatted

            Glide.with(itemView.context)
                .load(product.image.trim())
                .apply(RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop())
                .into(productImage)
        }
    }
}