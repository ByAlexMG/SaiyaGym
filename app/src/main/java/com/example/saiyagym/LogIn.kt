package com.example.saiyagym

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        setup()
    }

    private fun setup(){
        title= "LogIn"
        val botonEntrar = findViewById<MaterialButton>(R.id.buttonLogIn)
        val correo = findViewById<TextView>(R.id.editTextUsuario)
        val contra = findViewById<TextView>(R.id.editTextContraseña)
        val checkBoxRecordar = findViewById<CheckBox>(R.id.checkboxRecordar)

        botonEntrar.setOnClickListener {
            val email = correo.text.toString()
            val password = contra.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { signInTask ->
                        if (checkBoxRecordar.isChecked) {
                            saveUsernameToSharedPreferences(email)
                        }
                        if (signInTask.isSuccessful) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            currentUser?.let { user ->
                                val userDocument = db.collection("users").document(user.email!!)
                                userDocument.get().addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        val peso = document.getDouble("peso")
                                        val altura = document.getDouble("altura")
                                        val edad = document.getLong("edad")
                                        if (peso != null && altura != null && edad != null) {
                                            // Si los datos están llenos, ir a la actividad principal
                                            val intent = Intent(this, Principal::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else {
                                        // Si los datos aún no están llenos, ir a la actividad IntroducirDatos
                                        val intent = Intent(this, IntroducirDatos::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        startActivity(intent)
                                        finish()
                                    }
                                }
                            }
                        } else {
                            showAlert("Error", "Se ha producido un error al iniciar sesión")
                        }
                    }
            } else if (password.isEmpty()) {
                showAlert("Error", "Contraseña vacía")
            } else if (email.isEmpty()) {
                showAlert("Error", "Correo electrónico vacío")
            }else{
         }
        }
    }

    private fun saveUsernameToSharedPreferences(username: String) {
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply()
    }

    private fun showAlert(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    fun IrAPasswordOlvidada(view: View) {
        val intent = Intent(this, PasswordOlvidada::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun IrASingIn(view: View) {
        val intent = Intent(this, SingIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
