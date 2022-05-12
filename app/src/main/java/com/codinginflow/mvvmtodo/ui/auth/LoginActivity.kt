package com.codinginflow.mvvmtodo.ui.auth

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.codinginflow.mvvmtodo.databinding.ActivityLoginBinding
import com.codinginflow.mvvmtodo.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        loader = ProgressBar(this)

        binding.apply {
            btnGoSignup.setOnClickListener {
                val intent = Intent(
                    this@LoginActivity, RegistrationActivity::class.java
                )
                startActivity(intent)
            }
            btnForgotPassword.setOnClickListener {
                val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }
            btnLogin.setOnClickListener {

                val email = inputEmail.text.toString().trim()
                val password = inputPassword.text.toString().trim()

                if (email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) {
                    inputEmail.error = "Email is invalid"
                    return@setOnClickListener
                }

                if (password.isEmpty() && password.length < 8) {
                    inputPassword.error = "Password is invalid"
                    return@setOnClickListener
                }
                loader.visibility = View.VISIBLE

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@LoginActivity) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            Log.d(TAG, "User's displayname $user?.displayName.toString()")
                            navigateToHome()
                        } else {
                            Log.d(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext,
                                "Authentication failed: ${task.exception?.message ?: "Null"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}