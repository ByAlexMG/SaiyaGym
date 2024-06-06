package com.example.saiyagym.firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.saiyagym.LogHelper
import com.example.saiyagym.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SingIn : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sing_in)

        setup()
    }

    private fun setup() {
        title = "Registro"
        val botonRegistrar = findViewById<Button>(R.id.buttonRegistrarse)
        val editTextUsuario = findViewById<EditText>(R.id.editTextCorreo)
        val editTextContrasena = findViewById<EditText>(R.id.editTextContraseña)
        val editTextContrasena2 = findViewById<EditText>(R.id.editTextContraseña2)

        botonRegistrar.setOnClickListener {
            val email = editTextUsuario.text.toString()
            val password = editTextContrasena.text.toString()
            val confirmPassword = editTextContrasena2.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            currentUser?.let { user ->
                                val userDocument = db.collection("users").document(user.uid)
                                val userData = hashMapOf(
                                    "email" to email,
                                    "rol" to "user"
                                )

                                userDocument.set(userData).addOnSuccessListener {
                                    val intent = Intent(this, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    startActivity(intent)
                                    LogHelper.saveChangeLog(this, "Usuario registrado", "INFO")
                                }.addOnFailureListener {
                                    LogHelper.saveChangeLog(this, "Error al registrar usuario", "ERROR")
                                    showAlert("Error", "Se ha producido un error al registrar los datos")
                                }
                            }
                        } else {
                            showAlert("Error", "Se ha producido un error al registrarse")
                        }
                    }
                } else {
                    showAlert("Error", "Las contraseñas no coinciden")
                }
            } else {
                    showAlert("Error", "Es necesario que todos los campos estén rellenos")
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        if (!isFinishing) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    fun IrALogIn(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}
