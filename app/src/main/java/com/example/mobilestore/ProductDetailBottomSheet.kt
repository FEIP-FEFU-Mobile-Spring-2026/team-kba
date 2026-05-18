package com.example.mobilestore

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.example.mobilestore.model.Product

class ProductDetailBottomSheet : BottomSheetDialogFragment() {

    private var selectedSize: String = ""
    private var product: Product? = null
    private lateinit var btnSizeXXS: MaterialButton
    private lateinit var btnSizeXS: MaterialButton
    private lateinit var btnSizeS: MaterialButton
    private lateinit var btnSizeM: MaterialButton
    private lateinit var btnSizeL: MaterialButton
    private lateinit var btnSizeXL: MaterialButton

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

        btnSizeXXS = view.findViewById(R.id.btnSizeXXS)
        btnSizeXS = view.findViewById(R.id.btnSizeXS)
        btnSizeS = view.findViewById(R.id.btnSizeS)
        btnSizeM = view.findViewById(R.id.btnSizeM)
        btnSizeL = view.findViewById(R.id.btnSizeL)
        btnSizeXL = view.findViewById(R.id.btnSizeXL)

        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        val btnAddToCart =
            view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAddToCart)

        product?.let {
            view.findViewById<android.widget.TextView>(R.id.productDetailTitle).text = it.title
//            view.findViewById<android.widget.TextView>(R.id.productDetailDescription).text = it.description
            view.findViewById<android.widget.TextView>(R.id.productFullDescription).text =
                it.fullText

            val priceFormatted = String.format("%,d ₽", it.price.toLong()).replace(",", " ")
            btnAddToCart.text = "В корзину: $priceFormatted"

            Glide.with(requireContext())
                .load(it.image.trim())
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .centerCrop()
                )
                .into(view.findViewById(R.id.productDetailImage))
        } ?: run {
            Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        setupSizeButtons()

        btnAddToCart.setOnClickListener {
            if (selectedSize.isEmpty()) {
                Toast.makeText(requireContext(), "Выберите размер", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(
                requireContext(),
                "Товар добавлен в корзину:\n${product?.title}\nРазмер: $selectedSize",
                Toast.LENGTH_LONG
            ).show()

            dismiss()
        }
    }

    private fun setupSizeButtons() {
        selectButton(btnSizeM)

        btnSizeXXS.setOnClickListener { selectButton(btnSizeXXS) }
        btnSizeXS.setOnClickListener { selectButton(btnSizeXS) }
        btnSizeS.setOnClickListener { selectButton(btnSizeS) }
        btnSizeM.setOnClickListener { selectButton(btnSizeM) }
        btnSizeL.setOnClickListener { selectButton(btnSizeL) }
        btnSizeXL.setOnClickListener { selectButton(btnSizeXL) }
    }

    private fun selectButton(button: MaterialButton) {
        resetAllButtons()

        button.setTextColor(Color.WHITE)
        button.strokeColor =
            ContextCompat.getColorStateList(requireContext(), R.color.chip_selected_stroke)
        button.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.chip_selected_background)

        selectedSize = button.text.toString()
    }

    private fun resetAllButtons() {
        val chipStroke = ContextCompat.getColorStateList(requireContext(), R.color.chip_stroke)
        val chipBackground =
            ContextCompat.getColorStateList(requireContext(), R.color.chip_background)

        btnSizeXXS.setTextColor(Color.BLACK)
        btnSizeXXS.strokeColor = chipStroke
        btnSizeXXS.backgroundTintList = chipBackground

        btnSizeXS.setTextColor(Color.BLACK)
        btnSizeXS.strokeColor = chipStroke
        btnSizeXS.backgroundTintList = chipBackground

        btnSizeS.setTextColor(Color.BLACK)
        btnSizeS.strokeColor = chipStroke
        btnSizeS.backgroundTintList = chipBackground

        btnSizeM.setTextColor(Color.BLACK)
        btnSizeM.strokeColor = chipStroke
        btnSizeM.backgroundTintList = chipBackground

        btnSizeL.setTextColor(Color.BLACK)
        btnSizeL.strokeColor = chipStroke
        btnSizeL.backgroundTintList = chipBackground

        btnSizeXL.setTextColor(Color.BLACK)
        btnSizeXL.strokeColor = chipStroke
        btnSizeXL.backgroundTintList = chipBackground
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }
}