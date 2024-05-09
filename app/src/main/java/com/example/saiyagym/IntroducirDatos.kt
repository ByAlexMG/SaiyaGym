package com.example.saiyagym;

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class IntroducirDatos : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.introducir_datos)

        val spinnerGenero: Spinner = findViewById(R.id.spinnerGenero)
        val generoOptions = listOf("Hombre","Mujer", "Otro")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, generoOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        val buttonGuardarCambios: Button = findViewById(R.id.buttonGuardarCambios)
        buttonGuardarCambios.setOnClickListener {
            guardarDatos(spinnerGenero.selectedItem.toString())
        }
    }

    private fun guardarDatos(genero: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val editTextPeso = findViewById<EditText>(R.id.editTextPeso)
            val editTextAltura = findViewById<EditText>(R.id.editTextAltura)
            val editTextEdad = findViewById<EditText>(R.id.editTextEdad)

            val peso = editTextPeso.text.toString().toFloatOrNull()
            val altura = editTextAltura.text.toString().toFloatOrNull()
            val edad = editTextEdad.text.toString().toIntOrNull()

            if (peso != null && altura != null && edad != null) {
                val userData = hashMapOf(
                    "email" to user.email,
                    // No creo que sea muy seguro mostrar esto en la bd la verdad
                    // "ID" to user.uid,
                    "peso" to peso,
                    "altura" to altura,
                    "edad" to edad,
                    "genero" to genero
                )

                val userDocument = db.collection("users").document(user.uid)
                userDocument.set(userData)
                    .addOnSuccessListener {
                        val intent = Intent(this, ElegirMeta::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Faltan campos por rellenar", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
