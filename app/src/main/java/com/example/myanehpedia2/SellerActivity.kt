package com.example.myanehpedia2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myanehpedia2.ProductSellerAdapter
import com.example.myanehpedia2.api.RetrofitClient
import com.example.myanehpedia2.databinding.ActivitySellerBinding
import com.example.myanehpedia2.request.ProductRequest
import com.example.myanehpedia2.response.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SellerActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySellerBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var productSellerAdapter: ProductSellerAdapter
    private var productList: ArrayList<ProductResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize SharedPreferences logout
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Set up Toolbar
        setSupportActionBar(binding.toolbar)

        productSellerAdapter = ProductSellerAdapter(productList)
        binding.recyclerViewSeller.apply {
            layoutManager = LinearLayoutManager(this@SellerActivity)
            adapter = productSellerAdapter
        }

        productSellerAdapter.onEditClickListener = { product ->
            showEditProductDialog(product)
        }

        productSellerAdapter.onDeleteClickListener = { product ->
            showDeleteConfirmationDialog(product)
        }

        // Load data dari API
        loadProducts()

        binding.btnTambahBarang.setOnClickListener {
            showAddProductDialog()
        }
    }

    private fun loadProducts() {
        RetrofitClient.apiService.getProducts().enqueue(object : Callback<List<ProductResponse>> {
            override fun onResponse(call: Call<List<ProductResponse>>, response: Response<List<ProductResponse>>) {
                if (response.isSuccessful) {
                    // Update list
                    response.body()?.let {
                        productList.clear()
                        productList.addAll(it)
                        productSellerAdapter.notifyDataSetChanged()
                    }
                } else {
                    showMessage("Failed to load products.")
                }
            }

            override fun onFailure(call: Call<List<ProductResponse>>, t: Throwable) {
                showMessage("Error: ${t.message}")
            }
        })
    }


    // Handle toolbar menu item actions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)  // Inflate the logout menu
        return true
    }

    // Handle item click actions
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                SharedPrefHelper.clearUserData(this)
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Show a simple message
    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Tambah Barang")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val nama = dialogView.findViewById<EditText>(R.id.etNamaBarang).text.toString()
                val harga = dialogView.findViewById<EditText>(R.id.etHargaBarang).text.toString().toIntOrNull()
                val stok = dialogView.findViewById<EditText>(R.id.etStokBarang).text.toString().toIntOrNull()
                val gambar = dialogView.findViewById<EditText>(R.id.etGambarBarang).text.toString()

                if (!nama.isNullOrEmpty() && harga != null && stok != null) {
                    val productRequest = ProductRequest(nama, harga, stok, gambar)
                    addProductToDatabase(productRequest) // Kirim data ke API
                } else {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        dialog.show()
    }
    private fun showEditProductDialog(product: ProductResponse) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_product, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Barang")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val nama = dialogView.findViewById<EditText>(R.id.etNamaBarang).text.toString()
                val harga = dialogView.findViewById<EditText>(R.id.etHargaBarang).text.toString().toIntOrNull()
                val stok = dialogView.findViewById<EditText>(R.id.etStokBarang).text.toString().toIntOrNull()
                val gambar = dialogView.findViewById<EditText>(R.id.etGambarBarang).text.toString()

                if (!nama.isNullOrEmpty() && harga != null && stok != null) {
                    val productRequest = ProductRequest(nama, harga, stok, gambar)
                    updateProductInDatabase(product._id, productRequest) // Kirim perubahan ke API
                } else {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .create()

        // Isi field dialog dengan data produk yang ada
        dialogView.findViewById<EditText>(R.id.etNamaBarang).setText(product.nama)
        dialogView.findViewById<EditText>(R.id.etHargaBarang).setText(product.harga.toString())
        dialogView.findViewById<EditText>(R.id.etStokBarang).setText(product.stok.toString())
        dialogView.findViewById<EditText>(R.id.etGambarBarang).setText(product.gambar)

        dialog.show()
    }
    private fun showDeleteConfirmationDialog(product: ProductResponse) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Produk")
            .setMessage("Apakah anda yakin ingin menghapus produk ${product.nama}?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteProduct(product._id) // Panggil fungsi untuk menghapus produk
            }
            .setNegativeButton("Batal", null)
            .show()
    }
    private fun addProductToDatabase(productRequest: ProductRequest) {
        RetrofitClient.apiService.addProduct(productRequest).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SellerActivity, "Barang berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    // Refresh list produk
                    loadProducts()
                } else {
                    Toast.makeText(this@SellerActivity, "Gagal menambahkan barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@SellerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateProductInDatabase(productId: String, productRequest: ProductRequest) {
        RetrofitClient.apiService.updateProduct(productId, productRequest).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SellerActivity, "Barang berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    // Refresh list produk setelah update
                    loadProducts()
                } else {
                    Toast.makeText(this@SellerActivity, "Gagal memperbarui barang", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@SellerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun deleteProduct(productId: String) {
        RetrofitClient.apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@SellerActivity, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                    loadProducts() // Refresh data setelah produk dihapus
                } else {
                    Toast.makeText(this@SellerActivity, "Gagal menghapus produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@SellerActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}