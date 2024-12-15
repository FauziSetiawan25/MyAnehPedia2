package com.example.myanehpedia2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myanehpedia2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)  // Set the toolbar as ActionBar

        // Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.beranda -> {
                    replaceFragment(BerandaFragment())
                    true
                }
                R.id.pesanan -> {
                    replaceFragment(PesananFragment())
                    true
                }
                else -> false
            }
        }

        // Check user role
        val role = SharedPrefHelper.getUserRole(this)
        if (role == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        if (role == "Penjual"){
            startActivity(Intent(this, SellerActivity::class.java))
            finish()
        }

        binding.toolbar.setNavigationOnClickListener {
            SharedPrefHelper.clearUserData(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Inflate the menu (including logout button) into the toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // Handle item click events (for logout)
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

    // Replace the current fragment
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }
}