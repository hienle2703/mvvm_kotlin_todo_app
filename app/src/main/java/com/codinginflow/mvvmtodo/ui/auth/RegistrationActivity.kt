package com.codinginflow.mvvmtodo.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codinginflow.mvvmtodo.databinding.ActivityRegistrationBinding
import com.codinginflow.mvvmtodo.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

val TAG: String = "Regist Activity"

public class RegistrationActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassword: String

    private lateinit var auth: FirebaseAuth

    private lateinit var loader: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        auth = Firebase.auth
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
//                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
//                        if (it.isComplete) {
//                            val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
//                            startActivity(intent)
//                            finish()
//                            loader.visibility = View.GONE
//                        } else {
//                            val error = it.exception.toString()
//                            Toast.makeText(
//                                this@RegistrationActivity,
//                                "Registration failed: $error",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                            loader.visibility = View.GONE
//                        }

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this@RegistrationActivity) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val user = auth.currentUser
                                Log.d(TAG, user.toString())

                               val intent = Intent(this@RegistrationActivity, HomeActivity::class.java)
                                startActivity(intent)
                                finish()
                                loader.visibility = View.GONE
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                }
            }
        }
    }

}