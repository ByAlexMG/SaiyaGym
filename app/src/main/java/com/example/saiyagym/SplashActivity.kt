package com.example.saiyagym

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            val username = getUsernameFromSharedPreferences()
            if (username != null) {
                // Si hay un nombre de usuario guardado, inicia sesión automáticamente
                val intent = Intent(this, Principal::class.java)
                startActivity(intent)
            } else {
                // Si no hay un nombre de usuario guardado, lleva al usuario a la pantalla de inicio de sesión
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
