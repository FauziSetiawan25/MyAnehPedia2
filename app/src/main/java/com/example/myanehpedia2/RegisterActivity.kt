package com.example.myanehpedia2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myanehpedia2.api.RetrofitClient
import com.example.myanehpedia2.databinding.ActivityRegisterBinding
import com.example.myanehpedia2.request.RegisterRequest
import com.example.myanehpedia2.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding  // Deklarasikan binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsernameRegister.text.toString()
            val password = binding.etPasswordRegister.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                registerUser(username, password)
            } else {
                Toast.makeText(this, "Harap lengkapi semua data", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, password: String) {
        // Membuat objek request untuk dikirim ke API
        val registerRequest = RegisterRequest(username, password, "Pembeli")

        // Panggil API untuk registrasi
        RetrofitClient.apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.success == true) {
                        // Jika registrasi berhasil
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Jika response API menunjukkan kegagalan
                        Toast.makeText(this@RegisterActivity, registerResponse?.message ?: "Registrasi gagal", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Jika terjadi kesalahan pada server
                    Toast.makeText(this@RegisterActivity, "Server error, coba lagi.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                // Tangani kegagalan request (misalnya masalah jaringan)
                Toast.makeText(this@RegisterActivity, "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show()
            }
        })
    }
}