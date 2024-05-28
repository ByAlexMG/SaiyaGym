package com.example.saiyagym
import com.auth0.android.jwt.JWT

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.saiyagym.firebase.LoginActivity
import com.example.saiyagym.principal.Principal
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val token = getTokenFromSharedPreferences()
            if (token != null) {
                val userId = getUserIdFromToken(token)
                if (userId != null) {
                    checkUserCategories(userId)
                } else {
                    navigateToLogin()
                }
            } else {
                navigateToLogin()
            }
        }, SPLASH_TIME_OUT)
    }

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }

    private fun getUserIdFromToken(token: String): String? {
        return try {
            val jwt = JWT(token)
            jwt.getClaim("user_id").asString()
        } catch (e: Exception) {
            null
        }
    }

    private fun checkUserCategories(userId: String) {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                if (document.contains("categoria")) {
                    navigateToPrincipal()
                } else {
                    navigateToLogin()
                }
            } else {
                navigateToLogin()
            }
        }.addOnFailureListener {
            navigateToLogin()
        }
    }

    private fun navigateToPrincipal() {
        val intent = Intent(this, Principal::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
