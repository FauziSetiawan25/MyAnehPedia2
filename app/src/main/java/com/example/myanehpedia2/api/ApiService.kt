package com.example.myanehpedia2.api

import com.example.myanehpedia2.data.User
import com.example.myanehpedia2.request.ProductRequest
import com.example.myanehpedia2.request.RegisterRequest
import com.example.myanehpedia2.response.ProductResponse
import com.example.myanehpedia2.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.Path

interface ApiService {
    @GET("User")
    suspend fun getUserList(): List<User>

    @POST("User")
    fun registerUser(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @GET("Sell")
    fun getProducts(): retrofit2.Call<List<ProductResponse>>

    @POST("Sell")
    fun addProduct(@Body product: ProductRequest): Call<ProductResponse>

    @DELETE("Sell/{id}")
    fun deleteProduct(@Path("id") productId: String): Call<Void>

    @POST("Sell/{id}")
    fun updateProduct(@Path("id") productId: String, @Body product: ProductRequest): Call<ProductResponse>

    @POST("Sell/{id}")
    fun updateProductStock(@Path("id") productId: String, @Body updatedProduct: ProductResponse): Call<ProductResponse>

}