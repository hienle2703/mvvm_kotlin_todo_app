package com.codinginflow.mvvmtodo.ui

import android.content.Intent
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.ui.auth.LoginActivity

class MainActivity : AppCompatActivity() {

    private val SPLASH = 3000
    private lateinit var topAnim: Animation
    private lateinit var botAnim: Animation
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.statusBarColor = Color.WHITE

        setContentView(R.layout.activity_main)

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        botAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)
        imageView = findViewById(R.id.imageView)
        textView = findViewById(R.id.text)


        imageView.animation = topAnim
        textView.animation = botAnim

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                val intent = Intent(
                    this@MainActivity, LoginActivity::class.java
                )
                startActivity(intent)
                finish()
            }

        }, SPLASH.toLong())
    }
}