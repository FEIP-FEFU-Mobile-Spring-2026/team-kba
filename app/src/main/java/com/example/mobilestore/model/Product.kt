package com.example.mobilestore.model

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: List<String>,
    val categoryId: String,
    val sizes: List<Size>,
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        readSizesFromParcel(parcel),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(shortDescription)
        parcel.writeString(longDescription)
        parcel.writeInt(priceInKopecks)
        parcel.writeString(imageUrl)
        parcel.writeStringList(tags)
        parcel.writeString(categoryId)
        writeSizesToParcel(parcel, sizes)
        parcel.writeString(material)
        parcel.writeString(weight)
        parcel.writeString(season)
        parcel.writeString(countryOfOrigin)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product = Product(parcel)
        override fun newArray(size: Int): Array<Product?> = arrayOfNulls(size)
    }
}

private fun writeSizesToParcel(parcel: Parcel, sizes: List<Size>) {
    parcel.writeInt(sizes.size)
    sizes.forEach { size ->
        parcel.writeString(size.id)
        parcel.writeString(size.name)
    }
}

private fun readSizesFromParcel(parcel: Parcel): List<Size> {
    val size = mutableListOf<Size>()
    val sizeCount = parcel.readInt()
    repeat(sizeCount) {
        size.add(Size(parcel.readString() ?: "", parcel.readString() ?: ""))
    }
    return size
}