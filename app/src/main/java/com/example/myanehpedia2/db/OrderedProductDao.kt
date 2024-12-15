package com.example.myanehpedia2.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderedProductDao {

    @Insert
    fun insertOrderedProduct(orderedProduct: OrderedProduct)

    @Query("SELECT * FROM ordered_products")
    fun getAllOrderedProductsLive(): LiveData<List<OrderedProduct>>
}
