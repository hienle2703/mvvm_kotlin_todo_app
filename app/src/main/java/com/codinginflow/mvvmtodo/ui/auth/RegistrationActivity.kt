package com.codinginflow.mvvmtodo.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.codinginflow.mvvmtodo.databinding.ActivityRegistrationBinding
import com.codinginflow.mvvmtodo.ui.HomeActivity
import com.google.firebase.auth.FirebaseAuth

public class RegistrationActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        mAuth = FirebaseAuth.getInstance()

        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnGoBack.setOnClickListener {
                finish()
            }

            btnSignUp.setOnClickListener {
                email = inputEmail.text.toString().trim()
                password = inputPassword.text.toString().trim()
                confirmPassword = inputConfirmPassword.text.toString().trim()

                val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()

                if (email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    inputEmail.error = "Email can not be empty!"
                    return@setOnClickListener
                }
                if (password.isEmpty()) {
                    inputPassword.error = "Password can not be empty!"
                    return@setOnClickListener
                }
                if (password != confirmPassword) {
                    inputConfirmPassword.error = "Confirm password not matched!"
                    return@setOnClickListener
                }

//                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//                    val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }

            }
        }
    }
}