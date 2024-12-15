package com.example.myanehpedia2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myanehpedia2.databinding.ItemOrderedProductBinding
import com.example.myanehpedia2.db.OrderedProduct

class OrderAdapter : ListAdapter<OrderedProduct, OrderAdapter.OrderViewHolder>(OrderedProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderedProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order)
    }

    inner class OrderViewHolder(private val binding: ItemOrderedProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: OrderedProduct) {
            binding.tvNamaProduk.text = order.productName
            binding.tvHargaProduk.text = "Rp ${order.productPrice}"
            binding.tvOrderDate.text = "Tanggal: ${order.orderDate}"

            // Load gambar produk
            Glide.with(binding.imageView.context).load(order.productImage).into(binding.imageView)
        }
    }

    class OrderedProductDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<OrderedProduct>() {
        override fun areItemsTheSame(oldItem: OrderedProduct, newItem: OrderedProduct): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: OrderedProduct, newItem: OrderedProduct): Boolean {
            return oldItem == newItem
        }
    }
}