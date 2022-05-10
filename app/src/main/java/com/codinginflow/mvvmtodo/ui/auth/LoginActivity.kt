package com.codinginflow.mvvmtodo.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.codinginflow.mvvmtodo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE


        val binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.apply {
            btnGoSignup.setOnClickListener {

                println("HELLO")
                val intent = Intent(
                    this@LoginActivity, RegistrationActivity::class.java
                )
                startActivity(intent)

            }
        }


    }
}