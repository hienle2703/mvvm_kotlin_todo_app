package com.codinginflow.mvvmtodo.ui

import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.ui.auth.LoginActivity

//@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
//    private lateinit var navController: NavController
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//
//        // nếu mà dùng mỗi findNavController() không trong MainActivity thì sẽ crash nên phải gọi thông qua supportFragmentManager
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.findNavController()
//
//        setupActionBarWithNavController(navController)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }


    private val SPLASH = 3000
    private lateinit var topAnim: Animation
    private lateinit var botAnim: Animation
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.windowInsetsController!!.hide(
            android.view.WindowInsets.Type.statusBars()
        )
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

//const val ADD_TASK_RESULT_OK = Activity.RESULT_FIRST_USER
//const val EDIT_TASK_RESULT_OK = Activity.RESULT_OK + 1 // to get the next one