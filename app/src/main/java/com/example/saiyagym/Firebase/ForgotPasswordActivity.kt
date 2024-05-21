package com.example.saiyagym.Firebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.saiyagym.LogHelper
import com.example.saiyagym.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgotPasswordActivity : AppCompatActivity() {
    private  lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_olvidada)
        val botonRegistrar = findViewById<Button>(R.id.buttonRegistrarse)
        val correo = findViewById<EditText>(R.id.editTextCorreo)
        botonRegistrar.setOnClickListener {

            sendPasswordReset(correo.text.toString())
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        firebaseAuth= Firebase.auth
    }

    private fun sendPasswordReset(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener() { task ->
                if(task.isSuccessful){
                    LogHelper.saveChangeLog(this, "Intento de recuperar contraseña", "INFO")
                    Toast.makeText(baseContext,"Correo enviado", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(baseContext,"El correo no está registrado", Toast.LENGTH_SHORT).show()
                }
            }
    }
    fun volverALogin(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}

