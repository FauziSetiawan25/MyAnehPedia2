package com.example.myanehpedia2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myanehpedia2.api.RetrofitClient
import com.example.myanehpedia2.databinding.ItemProductBinding
import com.example.myanehpedia2.db.AppDatabase
import com.example.myanehpedia2.db.OrderedProduct
import com.example.myanehpedia2.response.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductBuyerAdapter(private val list: ArrayList<ProductResponse>, private val context: Context) : RecyclerView.Adapter<ProductBuyerAdapter.ProductViewHolder>() {

    var onBeliSekarangClickListener: ((ProductResponse) -> Unit)? = null

    // Inisialisasi DAO Room Database
    private val orderedProductDao = AppDatabase.getDatabase(context).orderedProductDao()

    inner class ProductViewHolder(itemView: ItemProductBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView

        fun bind(productResponse: ProductResponse) {
            // Load gambar produk
            Glide.with(binding.imageView.context).load(productResponse.gambar).into(binding.imageView)

            binding.tvNamaProduk.text = productResponse.nama
            binding.tvHargaProduk.text = "Rp ${productResponse.harga}"
            binding.tvStokProduk.text = "Stok: ${productResponse.stok}"

            binding.btnBeli.setOnClickListener {
                // Menampilkan dialog konfirmasi
                showConfirmationDialog(productResponse)
            }
        }

        private fun showConfirmationDialog(productResponse: ProductResponse) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Konfirmasi Pembelian")
            builder.setMessage("Apakah Anda yakin ingin membeli produk ${productResponse.nama}?")

            builder.setPositiveButton("Ya") { dialog, _ ->
                // Mengurangi stok produk secara lokal
                if (productResponse.stok > 0) {
                    productResponse.stok -= 1
                    binding.tvStokProduk.text = "Stok: ${productResponse.stok}"

                    // Update stok ke API
                    updateStockInApi(productResponse)

                    // Menyimpan produk yang dibeli ke Room Database
                    saveProductToRoomDatabase(productResponse)

                    Toast.makeText(context, "Pembelian berhasil!", Toast.LENGTH_SHORT).show()
                    onBeliSekarangClickListener?.invoke(productResponse)
                } else {
                    Toast.makeText(context, "Stok habis!", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }

        private fun updateStockInApi(productResponse: ProductResponse) {
            // Memanggil API untuk memperbarui stok
            val call = RetrofitClient.apiService.updateProductStock(productResponse._id, productResponse)

            call.enqueue(object : Callback<ProductResponse> {
                override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {

                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    // Tangani kesalahan, misalnya kegagalan jaringan
                    Toast.makeText(context, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
                }
            })
        }
        private fun saveProductToRoomDatabase(productResponse: ProductResponse) {
            // Menyimpan data produk yang dibeli ke Room Database
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val currentDate = dateFormat.format(Date()) // Ambil tanggal saat ini

            val orderedProduct = OrderedProduct(
                productName = productResponse.nama,
                productPrice = productResponse.harga,
                orderDate = currentDate,
                productImage = productResponse.gambar
            )

            // Menyimpan data ke Room Database di thread background
            Thread {
                orderedProductDao.insertOrderedProduct(orderedProduct)
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Inflate layout item produk
        return ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Bind data produk ke item
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}