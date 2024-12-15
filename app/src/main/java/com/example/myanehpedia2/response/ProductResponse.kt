package com.example.myanehpedia2.response

data class ProductResponse(
    val _id: String,
    val nama: String,
    val harga: Int,
    var stok: Int,
    val gambar: String
)