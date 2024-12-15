package com.example.myanehpedia2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myanehpedia2.api.RetrofitClient
import com.example.myanehpedia2.databinding.FragmentBerandaBinding
import com.example.myanehpedia2.response.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private lateinit var productAdapter: ProductBuyerAdapter
    private val productList = ArrayList<ProductResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout fragment with ViewBinding
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        productAdapter = ProductBuyerAdapter(productList, requireContext())
        binding.recyclerView.adapter = productAdapter

        // Load products from API
        loadProducts()

        return binding.root
    }

    private fun loadProducts() {
        RetrofitClient.apiService.getProducts().enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(
                call: Call<List<ProductResponse>>,
                response: Response<List<ProductResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        productList.clear()
                        productList.addAll(it)
                        productAdapter.notifyDataSetChanged() // Update RecyclerView
                    }
                } else {
                    Toast.makeText(context, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                Toast.makeText(context, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}