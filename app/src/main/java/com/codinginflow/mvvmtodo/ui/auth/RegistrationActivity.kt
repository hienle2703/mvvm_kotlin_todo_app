package com.codinginflow.mvvmtodo.ui.auth

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codinginflow.mvvmtodo.databinding.ActivityRegistrationBinding
import com.codinginflow.mvvmtodo.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.view.*

public class RegistrationActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        mAuth = FirebaseAuth.getInstance()
        loader = ProgressBar(this)

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

                if (email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) {
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
                } else {
                    loader.visibility = View.VISIBLE
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isComplete) {
                            val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            loader.visibility = View.GONE
                        } else {
                            val error = it.exception.toString()
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Registration failed: $error",
                                Toast.LENGTH_SHORT
                            ).show()
                            loader.visibility = View.GONE
                        }

                    }
                }
            }
        }
    }
}