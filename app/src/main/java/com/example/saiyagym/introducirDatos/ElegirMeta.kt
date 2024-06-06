package com.example.saiyagym.introducirDatos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import com.example.saiyagym.principal.Principal
import com.example.saiyagym.R

class ElegirMeta : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.elegir_nivel)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val botonVolver: Button = findViewById(R.id.backButton)
        val botonTerminar: Button = findViewById(R.id.finishButton)

        botonVolver.setOnClickListener {
            val intent = Intent(this, IntroducirDatos::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        botonTerminar.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
                val selectedTag = selectedRadioButton.tag.toString()
                val selectedGoal = selectedTag.toDoubleOrNull()
                if (selectedGoal != null) {
                    saveSelectedGoal(selectedGoal)
                }
            } else {
                Toast.makeText(this, "Es necesaio rellenar todos los campos", Toast.LENGTH_SHORT).show()
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, Principal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }, 1000)
        }
    }

    private fun saveSelectedGoal(selectedGoal: Double) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.update("selectedGoal", selectedGoal)
                .addOnSuccessListener {
                    subtractMuscleFromGoal()
                }
        }
    }

    private fun subtractMuscleFromGoal() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val selectedGoal = document.getDouble("selectedGoal") ?: 0.0
                        val muscle = document.getDouble("musculo") ?: 0.0
                        val difference = selectedGoal - muscle
                        userDocRef.update("diferencia", difference)
                            .addOnSuccessListener {
                                updateCategoryBasedOnDifference(difference)
                            }
                    }
                }
        }
    }

    private fun updateCategoryBasedOnDifference(difference: Double) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)
            val category = when {
                difference <= -20 -> "cardio"
                difference > -20 && difference <= -5 -> "definicion"
                difference > -5 && difference < 10 -> "mantenimiento"
                difference >= 10 -> "volumen"
                else -> "mantenimiento"
            }
            userDocRef.update("categoria", category)
        }
    }

}

