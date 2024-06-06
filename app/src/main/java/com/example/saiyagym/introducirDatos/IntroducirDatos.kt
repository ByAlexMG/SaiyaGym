package com.example.saiyagym.introducirDatos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.saiyagym.LogHelper
import com.example.saiyagym.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.SetOptions

class IntroducirDatos : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introducir_datos)

        val spinnerGenero: Spinner = findViewById(R.id.spinnerGenero)
        val generoOptions = listOf("Hombre", "Mujer", "Otro")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, generoOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenero.adapter = adapter

        val editTextPeso = findViewById<EditText>(R.id.editTextPeso)
        val editTextAltura = findViewById<EditText>(R.id.editTextAltura)
        val editTextEdad = findViewById<EditText>(R.id.editTextEdad)
        val porcentajeTextView = findViewById<TextView>(R.id.porcentaje)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                calcularPorcentajeSiEsPosible(porcentajeTextView)
            }
        }

        editTextPeso.addTextChangedListener(textWatcher)
        editTextAltura.addTextChangedListener(textWatcher)
        editTextEdad.addTextChangedListener(textWatcher)

        val buttonGuardarCambios: Button = findViewById(R.id.buttonGuardarCambios)
        buttonGuardarCambios.setOnClickListener {
            guardarDatos(spinnerGenero.selectedItem.toString(), porcentajeTextView)
        }
    }

    fun calcularPorcentajeSiEsPosible(textViewPorcentaje: TextView) {
        val editTextPeso = findViewById<EditText>(R.id.editTextPeso)
        val editTextAltura = findViewById<EditText>(R.id.editTextAltura)
        val editTextEdad = findViewById<EditText>(R.id.editTextEdad)

        val peso = editTextPeso.text.toString().toFloatOrNull()
        val altura = editTextAltura.text.toString().toFloatOrNull()
        val edad = editTextEdad.text.toString().toIntOrNull()

        if (peso != null && altura != null && edad != null) {
            val spinnerGenero = findViewById<Spinner>(R.id.spinnerGenero)
            val genero = spinnerGenero.selectedItem.toString()
            calcularPorcentajeMasaMusuclar(peso, altura, edad, genero, textViewPorcentaje)
        }
    }

    private fun calcularPorcentajeMasaMusuclar(
        peso: Float,
        altura: Float,
        edad: Int,
        genero: String,
        textViewPorcentaje: TextView
    ) {
        val IMC = peso / (altura * altura / 10000)
        val grasa: Float

        if (genero == "Hombre") {
            grasa = (100 - (1.2 * IMC + 0.23 * edad - 16.2)).toFloat()
        } else {
            grasa = (100 - (1.2 * IMC + 0.23 * edad - 5.4)).toFloat()
        }

        textViewPorcentaje.text = "${grasa.toInt()}%"  // Convert the value to an integer and add "%"
    }

    private fun guardarDatos(genero: String, porcentajeTextView: TextView) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val editTextPeso = findViewById<EditText>(R.id.editTextPeso)
            val editTextAltura = findViewById<EditText>(R.id.editTextAltura)
            val editTextEdad = findViewById<EditText>(R.id.editTextEdad)

            val pesoStr = editTextPeso.text.toString()
            val alturaStr = editTextAltura.text.toString()
            val edadStr = editTextEdad.text.toString()
            val musculoStr = porcentajeTextView.text.toString().replace("%", "")

            // Verificar que los campos no estén vacíos
            if (pesoStr.isNotEmpty() && alturaStr.isNotEmpty() && edadStr.isNotEmpty() && musculoStr.isNotEmpty()) {
                val peso = pesoStr.toFloatOrNull()
                val altura = alturaStr.toFloatOrNull()
                val edad = edadStr.toIntOrNull()
                val musculo = musculoStr.toFloatOrNull()

                if (peso != null && altura != null && edad != null && musculo != null) {
                    val userData = hashMapOf(
                        "email" to user.email,
                        "peso" to peso,
                        "altura" to altura,
                        "edad" to edad,
                        "genero" to genero,
                        "musculo" to musculo
                    )

                    val userDocument = db.collection("users").document(user.uid)
                    userDocument.set(userData, SetOptions.merge())
                        .addOnSuccessListener {
                            val intent = Intent(this, ElegirMeta::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                            LogHelper.saveChangeLog(this, "Guardar datos", "INFO")
                        }
                        .addOnFailureListener { e ->
                            LogHelper.saveChangeLog(this, "Error al guardar dato", "ERROR")
                            val snackbar = Snackbar.make(findViewById(android.R.id.content), "Error al guardar los datos", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                } else {
                    val snackbar = Snackbar.make(findViewById(android.R.id.content), "Es necesario rellenar todos los campos con valores válidos", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            } else {
                val snackbar = Snackbar.make(findViewById(android.R.id.content), "Es necesario rellenar todos los campos", Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        }
    }
}
