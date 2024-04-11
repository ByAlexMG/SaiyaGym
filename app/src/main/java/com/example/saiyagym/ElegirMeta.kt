package com.example.saiyagym

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class ElegirMeta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.elegir_nivel)

        val botonVolver: Button = findViewById(R.id.backButton)
        botonVolver.setOnClickListener {
            val intent = Intent(this, IntroducirDatos::class.java)
            startActivity(intent)
        }
        val botonTerminar: Button = findViewById(R.id.finishButton)
        botonTerminar.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }

    }
}
