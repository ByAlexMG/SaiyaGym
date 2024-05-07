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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class Option3Fragment : Fragment() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_option3, container, false)

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

        // Layout para el diálogo
        val view = layoutInflater.inflate(R.layout.change_email_dialog, null)
        val currentEmailEditText = view.findViewById<EditText>(R.id.currentEmailEditText)
        val newEmailEditText = view.findViewById<EditText>(R.id.newEmailEditText)
        builder.setView(view)

        // Botón para cambiar el correo electrónico
        builder.setPositiveButton("Cambiar") { dialog, _ ->
            val currentEmail = currentEmailEditText.text.toString()
            val newEmail = newEmailEditText.text.toString()

            // Llamar a la función changeEmail con los correos electrónicos proporcionados
            changeEmail(currentEmail, newEmail)
            dialog.dismiss()
        }

        // Botón para cancelar
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        // Mostrar el diálogo
        builder.show()
    }
    private fun changeEmail(currentPassword: String, newEmail: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updateEmail(newEmail)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // El correo electrónico se ha actualizado con éxito
                                // Aquí puedes mostrar un mensaje de éxito o realizar alguna otra acción
                                showToast("Correo electrónico actualizado con éxito")
                            } else {
                                // No se pudo actualizar el correo electrónico
                                // Aquí puedes mostrar un mensaje de error o realizar alguna otra acción
                                showToast("Error al actualizar el correo electrónico")
                            }
                        }
                } else {
                    // La reautenticación falló, la contraseña actual es incorrecta
                    // Aquí puedes mostrar un mensaje de error o realizar alguna otra acción
                    showToast("El correo actual es incorrecto")
                }
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
