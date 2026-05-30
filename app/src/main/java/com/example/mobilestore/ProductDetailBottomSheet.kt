package com.example.mobilestore

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.example.mobilestore.model.Product
import com.example.mobilestore.model.Size

class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var selectedSize: Size? = null
    private var product: Product? = null
    private val sizeButtons = mutableMapOf<Size, TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        product = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("PRODUCT", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("PRODUCT")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.bottom_sheet_product_detail, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        val btnAddToCart = view.findViewById<MaterialButton>(R.id.btnAddToCart)
        val infoIcon = view.findViewById<View>(R.id.infoIcon)

        product?.let { product ->
            view.findViewById<TextView>(R.id.productDetailTitle).text = product.name
            view.findViewById<TextView>(R.id.productFullDescription).text = product.longDescription

            val priceInRubles = product.priceInKopecks / 100.0
            val priceFormatted = String.format("%,d ₽", priceInRubles.toInt()).replace(",", " ")
            btnAddToCart.text = "В корзину · $priceFormatted"

            Glide.with(requireContext())
                .load(product.imageUrl)
                .apply(RequestOptions()
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .centerCrop()
                )
                .into(view.findViewById(R.id.productDetailImage))

            setupTags(view, product.tags)
            setupSizeButtons(view, product.sizes)

            infoIcon.setOnClickListener {
                showExtraInfoDialog(product)
            }
        } ?: run {
            Toast.makeText(requireContext(), "Ошибка загрузки товара", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnAddToCart.setOnClickListener {
            if (selectedSize == null) {
                Toast.makeText(requireContext(), "Выберите размер", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(
                requireContext(),
                "Товар добавлен в корзину:\n${product?.name}\nРазмер: ${selectedSize?.name}",
                Toast.LENGTH_LONG
            ).show()
            dismiss()
        }
    }

    private fun setupTags(view: View, tags: List<String>) {
        val container = view.findViewById<ViewGroup>(R.id.tagsContainer)
        container?.removeAllViews()
        if (tags.isEmpty()) {
            container?.visibility = View.GONE
            return
        }
        container?.visibility = View.VISIBLE
        tags.forEach { tag ->
            val chip = TextView(requireContext()).apply {
                text = tag.uppercase()
                setBackgroundResource(R.drawable.bg_tag)
                setPadding(32, 8, 32, 8)
                textSize = 11f
                setTextColor(Color.WHITE)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            container?.addView(chip)
        }
    }

    private fun setupSizeButtons(view: View, sizes: List<Size>) {
        val container = view.findViewById<ViewGroup>(R.id.sizeContainer)
        container?.removeAllViews()
        sizeButtons.clear()

        // Список всех размеров по порядку
        val allSizes = listOf("XXS", "XS", "S", "M", "L", "XL")

        allSizes.forEach { sizeName ->
            val size = sizes.find { it.name == sizeName } ?: Size("custom_$sizeName", sizeName)

            val button = TextView(requireContext()).apply {
                text = size.name
                setBackgroundResource(R.drawable.bg_category_unselected)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setPadding(48, 12, 46, 12)
                textSize = 18f
                gravity = Gravity.CENTER
                maxLines = 1

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT  // Автоматическая высота
                ).apply {
                    marginEnd = 28  // Увеличен отступ справа
                }

                setOnClickListener {
                    selectSize(this, size)
                }
            }
            container?.addView(button)
            sizeButtons[size] = button
        }

        val defaultSize = sizeButtons.keys.find { it.name == "M" }
        defaultSize?.let { size ->
            sizeButtons[size]?.let { button ->
                selectSize(button, size)
            }
        }
    }

    private fun selectSize(button: TextView, size: Size) {
        sizeButtons.values.forEach {
            it.setBackgroundResource(R.drawable.bg_category_unselected)
            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        button.setBackgroundResource(R.drawable.bg_category_selected)
        button.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        selectedSize = size
    }

    private fun showExtraInfoDialog(product: Product) {
        val message = buildString {
            append("📦 Материал: ${product.material}\n")
            append("⚖️ Вес: ${product.weight}\n")
            append("🌤️ Сезон: ${product.season}\n")
            append("🌍 Страна: ${product.countryOfOrigin}")
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Характеристики")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}