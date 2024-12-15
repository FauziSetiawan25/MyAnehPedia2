package com.example.myanehpedia2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myanehpedia2.databinding.ItemProductSellerBinding
import com.example.myanehpedia2.response.ProductResponse

class ProductSellerAdapter (private val list:ArrayList<ProductResponse>): RecyclerView.Adapter<ProductSellerAdapter.ProductViewHolder>(){
    var onEditClickListener: ((ProductResponse) -> Unit)? = null
    var onDeleteClickListener: ((ProductResponse) -> Unit)? = null

    inner class ProductViewHolder(itemView: ItemProductSellerBinding): RecyclerView.ViewHolder(itemView.root){
        private val binding = itemView
        fun bind(productResponse: ProductResponse) {
            Glide.with(binding.imageView.context).load(productResponse.gambar).into(binding.imageView)

            binding.tvNamaProduk.text = productResponse.nama
            binding.tvHargaProduk.text = "Rp ${productResponse.harga}"
            binding.tvStokProduk.text = "Stok: ${productResponse.stok}"

            // Set onClickListener for edit and delete buttons
            binding.btnEdit.setOnClickListener {
                onEditClickListener?.invoke(productResponse)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClickListener?.invoke(productResponse)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSellerAdapter.ProductViewHolder {
        return ProductViewHolder(ItemProductSellerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductSellerAdapter.ProductViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount():Int = list.size
}