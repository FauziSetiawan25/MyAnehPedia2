package com.example.myanehpedia2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myanehpedia2.OrderAdapter
import com.example.myanehpedia2.databinding.FragmentPesananBinding
import com.example.myanehpedia2.db.AppDatabase
import com.example.myanehpedia2.db.OrderedProductDao

class PesananFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderedProductDao: OrderedProductDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPesananBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView

        // Setup RecyclerView dan Adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrderAdapter()
        recyclerView.adapter = orderAdapter

        // Mendapatkan instance DAO Room
        orderedProductDao = AppDatabase.getDatabase(requireContext()).orderedProductDao()

        // Observasi data dari Room Database menggunakan LiveData
        orderedProductDao.getAllOrderedProductsLive().observe(viewLifecycleOwner, Observer { orders ->
            orders?.let {
                // Update UI ketika data berubah
                orderAdapter.submitList(it)
            }
        })

        return binding.root
    }
}