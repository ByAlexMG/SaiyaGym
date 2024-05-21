package com.example.saiyagym

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.saiyagym.Firebase.LoginActivity
import com.example.saiyagym.Principal.Principal

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            val username = getUsernameFromSharedPreferences()
            if (username != null) {
                val intent = Intent(this, Principal::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun getUsernameFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", null)
    }
}
