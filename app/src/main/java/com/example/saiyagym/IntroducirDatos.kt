package com.example.saiyagym


import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Button

class IntroducirDatos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.introducir_datos)

        val spinnerGenero: Spinner = findViewById(R.id.spinnerGenero)
        val generoOptions = listOf("Mujer", "Hombre", "Otro")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, generoOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        val buttonGuardarCambios: Button = findViewById(R.id.buttonGuardarCambios)
        buttonGuardarCambios.setOnClickListener {
            val intent = Intent(this, ElegirMeta::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
