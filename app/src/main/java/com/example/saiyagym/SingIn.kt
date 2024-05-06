package com.example.saiyagym


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
            if(editTextUsuario.text.isNotEmpty()&& editTextContrasena.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextUsuario.text.toString(),
                    editTextContrasena.text.toString()).addOnCompleteListener {

                    if (it.isSuccessful){


                       db.collection("users").document(editTextUsuario.text.toString()).set(
                           hashMapOf("contraseña" to editTextContrasena.text.toString())
                       )

                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)

                    }else{
                        showAlert()
                    }
                }
            }else if( editTextContrasena.text.isEmpty()){
                AlertPassword()
            }
            else if(editTextUsuario.text.isEmpty()){
                AlertCorreo()
            }

        }
    }

    private fun showAlert() {
        if (!isFinishing) { // Verifica si la actividad no ha sido finalizada
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Se ha producido un error al registrarse")
            builder.setPositiveButton("Aceptar", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
    private fun AlertCorreo(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Correo electronico no valido o vacio")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun AlertPassword(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Contraseña vacia o no valida(debe tener al menos 6 caracteres, numeros y letras")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    fun IrALogIn(view: View) {
        val intent = Intent(this,LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
}