package com.example.myanehpedia2.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ordered_products")
data class OrderedProduct(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productName: String,
    val productPrice: Int,
    val orderDate: String,
    val productImage: String
)
