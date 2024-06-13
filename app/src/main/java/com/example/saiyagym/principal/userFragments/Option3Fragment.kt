package com.example.saiyagym.principal.userFragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.Fragment
import com.example.saiyagym.LogHelper
import com.example.saiyagym.principal.Principal
import com.example.saiyagym.R
import com.example.saiyagym.firebase.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Option3Fragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var button1: MaterialButton


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
        button1 = view.findViewById<MaterialButton>(R.id.button1)
        button1.setOnClickListener {

            showChangeEmailDialog()

        }

        closeButton.setOnClickListener {
            val sharedPreferences =
                requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear().apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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
        checkButtonVisibility()
        return view
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cambiar Contraseña")

        val view = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val currentPasswordEditText = view.findViewById<EditText>(R.id.currentPasswordEditText)
        val newPasswordEditText = view.findViewById<EditText>(R.id.newPasswordEditText)
        val newPasswordEditText2 = view.findViewById<EditText>(R.id.newPasswordEditText2)

        builder.setView(view)

        builder.setPositiveButton("Cambiar") { _, _ ->
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val newPassword2 = newPasswordEditText2.text.toString()

            if (newPassword == newPassword2) {
                changePassword(currentPassword, newPassword)
            } else {
                Snackbar.make(requireView(), "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(),R.color.textColorMoroso))

    }

    private fun checkButtonVisibility() {
        isAdmin { isAdmin ->
            if (isAdmin) {
                button1.visibility = View.GONE
            } else {
                button1.visibility = View.VISIBLE
            }
        }
    }
    private fun isAdmin(callback: (Boolean) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("rol")
                    callback(role != null && role == "admin")
                } else {
                    callback(false)
                }
            }.addOnFailureListener {
                callback(false)
            }
        } else {
            callback(false)
        }
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
                                LogHelper.saveChangeLog(
                                    requireContext(),
                                    "Contraseña actualizada",
                                    "INFO"
                                )

                                // Update the password in shared preferences
                                val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("password", newPassword)
                                editor.apply()

                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Contraseña actualizada",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.show()
                            } else {
                                LogHelper.saveChangeLog(
                                    requireContext(),
                                    "Error al actualizar contraseña",
                                    "ERROR"
                                )
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Error al actualizar",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.show()
                            }
                        }
                } else {
                    val snackbar = Snackbar.make(
                        requireView(),
                        "Contraseña actual incorrecta",
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
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

            changeEmail(currentEmail, currentPassword, newEmail)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.setOnDismissListener {
            checkButtonVisibility()
        }
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(), R.color.textColorMoroso))
    }



    private fun changeEmail(currentEmail: String, currentPassword: String, newEmail: String) {
        if (newEmail.endsWith("@saiyagym.com")) {
            Snackbar.make(requireView(), "Dominio inválido", Snackbar.LENGTH_SHORT).show()
            return
        }
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(currentEmail, currentPassword)
            .addOnCompleteListener { signInTask ->
                if (signInTask.isSuccessful) {
                    val user = auth.currentUser
                    user!!.updateEmail(newEmail)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Update the email field in the user's document
                                val userId = user.uid
                                val userDocRef = db.collection("users").document(userId)
                                userDocRef.update("email", newEmail)
                                    .addOnSuccessListener {
                                        LogHelper.saveChangeLog(
                                            requireContext(),
                                            "Correo Actualizado",
                                            "INFO"
                                        )
                                        // Update the email in shared preferences
                                        val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("email", newEmail)
                                        editor.apply()

                                        val snackbar = Snackbar.make(
                                            requireView(),
                                            "Correo Actualizado",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        snackbar.show()
                                    }
                                    .addOnFailureListener { e ->
                                        LogHelper.saveChangeLog(
                                            requireContext(),
                                            "Error al actualizar el correo",
                                            "ERROR"
                                        )
                                        val snackbar = Snackbar.make(
                                            requireView(),
                                            "Error al actualizar el correo",
                                            Snackbar.LENGTH_SHORT
                                        )
                                        snackbar.show()
                                    }
                            } else {
                                LogHelper.saveChangeLog(
                                    requireContext(),
                                    "Error al actualizar el correo",
                                    "ERROR"
                                )
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Error al actualizar el correo",
                                    Snackbar.LENGTH_SHORT
                                )
                                snackbar.show()
                            }
                        }
                } else {
                    val snackbar = Snackbar.make(
                        requireView(),
                        "Datos de sesión erróneos",
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }
            }
    }



    private fun calcularPorcentajeMasaMuscular(
        peso: Float,
        altura: Float,
        edad: Int,
        genero: String
    ): Float {
        val IMC = peso / (altura * altura / 10000)
        val grasa: Float

        grasa = if (genero == "Hombre") {
            (100 - (1.2 * IMC + 0.23 * edad - 16.2)).toFloat()
        } else {
            (100 - (1.2 * IMC + 0.23 * edad - 5.4)).toFloat()
        }

        return grasa
    }

    private fun showChangeMeasurementsDialog() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userDocument = db.collection("users").document(user.uid)

            userDocument.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentWeight = document.getDouble("peso")?.toString() ?: ""
                    val currentHeight = document.getDouble("altura")?.toString() ?: ""
                    val currentAge = document.getLong("edad")?.toString() ?: ""
                    val currentGender = document.getString("genero") ?: ""

                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Cambiar Medidas")

                    val view = layoutInflater.inflate(R.layout.change_measurements_dialog, null)
                    val weightEditText = view.findViewById<TextInputEditText>(R.id.weightEditText)
                    val heightEditText = view.findViewById<TextInputEditText>(R.id.heightEditText)
                    val ageEditText = view.findViewById<TextInputEditText>(R.id.ageEditText)
                    val genderSpinner = view.findViewById<AppCompatSpinner>(R.id.genderSpinner)
                    weightEditText.hint = "peso = $currentWeight"
                    heightEditText.hint = "altura = $currentHeight"
                    ageEditText.hint = "edad = $currentAge"

                    ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.gender_array,
                        android.R.layout.simple_spinner_item
                    ).also { adapter ->
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        genderSpinner.setAdapter(adapter)
                    }
                    val genderArray = resources.getStringArray(R.array.gender_array)
                    val genderIndex = genderArray.indexOf(currentGender)
                    if (genderIndex >= 0) {
                        genderSpinner.setSelection(genderIndex)
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
                            val musclePercentage = calcularPorcentajeMasaMuscular(weight, height, age, gender)
                            userDocument.update(
                                mapOf(
                                    "peso" to weight,
                                    "altura" to height,
                                    "edad" to age,
                                    "genero" to gender,
                                    "musculo" to musclePercentage
                                )
                            )
                                .addOnSuccessListener {
                                    LogHelper.saveChangeLog(
                                        requireContext(),
                                        "Medidas y masa muscular actualizadas",
                                        "INFO"
                                    )
                                    val snackbar = Snackbar.make(requireView(), "Medidas actualizadas", Snackbar.LENGTH_SHORT)
                                    snackbar.show()
                                    subtractMuscleFromGoal()
                                }
                                .addOnFailureListener { e ->
                                    LogHelper.saveChangeLog(
                                        requireContext(),
                                        "Error al actualizar las medidas y masa muscular",
                                        "ERROR"
                                    )
                                    val snackbar = Snackbar.make(requireView(), "Error al actualizar medidas", Snackbar.LENGTH_SHORT)
                                    snackbar.show()
                                }
                        } else {
                            val snackbar = Snackbar.make(requireView(), "Valores no validos", Snackbar.LENGTH_SHORT)
                            snackbar.show()
                        }
                    }
                    builder.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val dialog = builder.create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(requireContext(),R.color.textColorMoroso))
                }
            }
        }
    }

    private fun subtractMuscleFromGoal() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)
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
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val userDocRef = db.collection("users").document(userId)
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