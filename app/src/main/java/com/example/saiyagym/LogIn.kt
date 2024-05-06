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

class LoginActivity : AppCompatActivity() {

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
            if(correo.text.isNotEmpty()&& contra.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(correo.text.toString(),
                    contra.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful){
                        if (checkBoxRecordar.isChecked) {
                            saveUsernameToSharedPreferences(correo)
                        }
                        val intent = Intent(this, IntroducirDatos::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()


                    }else{
                        showAlert()
                    }
                }
            }else if(contra.text.isEmpty()){
                AlertPassword()
            }
            else if(correo.text.isEmpty()){
                AlertCorreo()
            }
        }
    }
    private fun saveUsernameToSharedPreferences(username: TextView) {
        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username.toString())
        editor.apply()
    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("se ha producido un error al iniciar sesion")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun AlertCorreo(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Datos erroneos")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun AlertPassword(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Datos erroneos")
        builder.setPositiveButton("Aceptar",null)
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
