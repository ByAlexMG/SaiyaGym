package com.example.saiyagym.Firebase

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

    private fun setup(){
        title= "Registro"
        val botonRegistrar = findViewById<Button>(R.id.buttonRegistrarse)
        val editTextUsuario = findViewById<EditText>(R.id.editTextCorreo)
        val editTextContrasena = findViewById<EditText>(R.id.editTextContraseña)

        botonRegistrar.setOnClickListener {
            if(editTextUsuario.text.isNotEmpty() && editTextContrasena.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextUsuario.text.toString(),
                    editTextContrasena.text.toString()).addOnCompleteListener { task ->

                    if (task.isSuccessful){
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        currentUser?.let { user ->
                            val userDocument = db.collection("users").document(user.uid)
                            val userData = hashMapOf(
                                "email" to editTextUsuario.text.toString(),
                                "contraseña" to editTextContrasena.text.toString(),
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
            } else if(editTextContrasena.text.isEmpty()) {
                showAlert("Error", "Contraseña vacía")
            } else if(editTextUsuario.text.isEmpty()) {
                showAlert("Error", "Correo electrónico vacío")
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