package com.example.saiyagym


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SingIn : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sing_in)
    }

    fun IrALogIn(view: View) {
        val intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
    }
}