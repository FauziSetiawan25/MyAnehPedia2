package com.example.myanehpedia2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myanehpedia2.api.RetrofitClient
import com.example.myanehpedia2.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsernameLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                loginUser(username, password)
            } else {
                Toast.makeText(this, "Username or password is empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvDaftar.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun loginUser(username: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getUserList()
                val user = response.find { it.username == username && it.password == password }
                if (user != null) {
                    runOnUiThread {
                        // Save user data to SharedPreferences
                        SharedPrefHelper.saveUserData(applicationContext, user.username, user.role)

                        // Navigate to the respective activity based on role
                        if (user.role == "Pembeli") {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        } else {
                            startActivity(Intent(this@LoginActivity, SellerActivity::class.java))
                        }
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}