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
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import java.util.Date

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
                                saveChangeLog(user.uid)
                                val snackbar = Snackbar.make(requireView(), "Contraseña actualizada", Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            } else {
                                val snackbar = Snackbar.make(requireView(), "Error al actualizar", Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            }
                        }
                } else {
                    val snackbar = Snackbar.make(requireView(), "Contraseña actual incorrecta", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }
    }

    private fun saveChangeLog(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val logEntry = hashMapOf(
            "UID" to userId,
            "fecha" to Date(),
            "action" to "Contraseña Cambiada",
            "tipo" to "INFO"
        )

        val docRef = db.collection("log").document("log")

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Si el documento existe, añade el nuevo log al array de logs existente
                docRef.update("logs", FieldValue.arrayUnion(logEntry))
                    .addOnSuccessListener {
                        // Log guardado con éxito
                    }
                    .addOnFailureListener { e ->
                        // Maneja cualquier error al guardar el log
                        val snackbar = Snackbar.make(requireView(), "Error al guardar el log", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
            } else {
                // Si el documento no existe, crea un nuevo documento con el primer log
                val logs = arrayListOf(logEntry)
                docRef.set(hashMapOf("logs" to logs))
                    .addOnSuccessListener {
                        // Log guardado con éxito
                    }
                    .addOnFailureListener { e ->
                        // Maneja cualquier error al guardar el log
                        val snackbar = Snackbar.make(requireView(), "Error al guardar el log", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
            }
        }.addOnFailureListener { e ->
            // Maneja cualquier error al obtener el documento
            val snackbar = Snackbar.make(requireView(), "Error al obtener el documento de log", Snackbar.LENGTH_SHORT)
            snackbar.show()
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
                                val snackbar = Snackbar.make(requireView(), "Correo Actualizado", Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            } else {
                                val snackbar = Snackbar.make(requireView(), "Error al actualizar el correo", Snackbar.LENGTH_SHORT)
                                snackbar.show()
                            }
                        }
                } else {
                    val snackbar = Snackbar.make(requireView(), "Datos de sesión erroneos", Snackbar.LENGTH_SHORT)
                    snackbar.show()
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
            val genderSpinner = view.findViewById<Spinner>(R.id.genderSpinner)


            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.gender_array,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genderSpinner.adapter = adapter
            }

            builder.setView(view)

            builder.setPositiveButton("Guardar") { _, _ ->
                val weightString = weightEditText.text.toString()
                val heightString = heightEditText.text.toString()
                val ageString = ageEditText.text.toString()
                val gender = genderSpinner.selectedItem.toString()

                val weight = weightString.toFloatOrNull()
                val height = heightString.toFloatOrNull()
                val age = ageString.toIntOrNull()

                if (weight != null && height != null && age != null) {
                    userDocument.update(
                        mapOf(
                            "peso" to weight,
                            "altura" to height,
                            "edad" to age,
                            "genero" to gender
                        )
                    )
                        .addOnSuccessListener {
                            val snackbar = Snackbar.make(requireView(), "Medidas actualizadas correctamente", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                        .addOnFailureListener { e ->
                            val snackbar = Snackbar.make(requireView(), "Error al actualizar las medidas", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                } else {
                    val snackbar = Snackbar.make(requireView(), "Los valores de peso, altura y edad deben ser números válidos", Snackbar.LENGTH_SHORT)
                    snackbar.show()
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
