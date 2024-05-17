package com.example.saiyagym

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast

class ElegirMeta : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        }

        botonTerminar.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            if (selectedRadioButtonId != -1) {
                val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
                val selectedTag = selectedRadioButton.tag.toString()
                saveSelectedGoal(selectedTag)
            } else {
                Toast.makeText(this, "Por favor, selecciona una meta", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this, Principal::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun saveSelectedGoal(selectedTag: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.update("selectedGoal", selectedTag)
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar la meta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
