package com.example.saiyagym

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Option3Fragment : Fragment() {
    private  lateinit var firebaseAuth:FirebaseAuth
    private lateinit var db: FirebaseFirestore
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
         db = FirebaseFirestore.getInstance()
        val view = inflater.inflate(R.layout.fragment_option3, container, false)

        val medidasBoton = view.findViewById<Button>(R.id.cambiarMedidas)
        medidasBoton.setOnClickListener {
            showChangeMeasurementsDialog()
        }
        val closeButton = view.findViewById<MaterialButton>(R.id.CerrarSesion)
        val button1 = view.findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            showChangeEmailDialog()
        }
        closeButton.setOnClickListener {
            val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear().apply()
            requireActivity().finish()
        }

        val switch: Switch = view.findViewById(R.id.switchTema)

        val principal = activity as Principal
        val sp = principal.getSharedPreferences("my_preferences", MODE_PRIVATE)
        val editor = sp.edit()


        val theme = sp.getInt("theme", 1)
        switch.isChecked = theme != 1


        switch.setOnClickListener {
            if (switch.isChecked) {
                editor.putInt("theme", 0)
            } else {
                editor.putInt("theme", 1)
            }
            editor.apply()
            principal.setDayNight()
        }

        val changePasswordButton = view.findViewById<MaterialButton>(R.id.button2)

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        return view
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar Contraseña")

        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordEditText = view.findViewById<EditText>(R.id.currentPasswordEditText)
        val newPasswordEditText = view.findViewById<EditText>(R.id.newPasswordEditText)

        builder.setView(view)

        builder.setPositiveButton("Cambiar") { _, _ ->
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            changePassword(currentPassword, newPassword)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)


        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                showToast("Contraseña actualizada con éxito")
                            } else {
                                showToast("Error al actualizar la contraseña")
                            }
                        }
                } else {
                    showToast("La contraseña actual es incorrecta")
                }
            }
    }

    private fun showChangeEmailDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Cambiar Correo Electrónico")

        val view = layoutInflater.inflate(R.layout.change_email_dialog, null)
        val currentEmailEditText = view.findViewById<EditText>(R.id.currentEmailEditText)
        val newEmailEditText = view.findViewById<EditText>(R.id.newEmailEditText)
        val currentPasswordEditText = view.findViewById<EditText>(R.id.currentPasswordEditText)
        builder.setView(view)

        builder.setPositiveButton("Cambiar") { dialog, _ ->
            val currentEmail = currentEmailEditText.text.toString()
            val currentPassword = currentPasswordEditText.text.toString()
            val newEmail = newEmailEditText.text.toString()

            changeEmail(currentEmail,currentPassword, newEmail)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    private fun changeEmail(currentEmail: String, currentPassword: String, newEmail: String) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {

                    auth.currentUser!!.updateEmail(newEmail)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {

                                showToast("Correo actualizado")
                            } else {
                                showToast("Error al actualizar el correo")
                            }
                        }
                } else {

                    showToast("Datos de sesión fallidos")
                }
            }
    }
    private fun showChangeMeasurementsDialog() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Cambiar Medidas")

            val view = layoutInflater.inflate(R.layout.change_measurements_dialog, null)
            val weightEditText = view.findViewById<EditText>(R.id.weightEditText)
            val heightEditText = view.findViewById<EditText>(R.id.heightEditText)
            val ageEditText = view.findViewById<EditText>(R.id.ageEditText)
            val genderEditText = view.findViewById<EditText>(R.id.genderEditText)
            val resultTextView = view.findViewById<TextView>(R.id.resultTextView)

            builder.setView(view)

            builder.setPositiveButton("Guardar") { _, _ ->
                val weight = weightEditText.text.toString()
                val height = heightEditText.text.toString()
                val age = ageEditText.text.toString()
                val gender = genderEditText.text.toString()
                
                userDocument.update(
                    mapOf(
                        "peso" to weight,
                        "altura" to height,
                        "edad" to age,
                        "genero" to gender
                    )
                )
                    .addOnSuccessListener {
                        val result = "Peso: $weight\nAltura: $height\nEdad: $age\nGénero: $gender"
                        resultTextView.text = result
                        Toast.makeText(requireContext(), "Medidas actualizadas correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error al actualizar las medidas: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
