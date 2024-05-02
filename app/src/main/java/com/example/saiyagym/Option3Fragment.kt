package com.example.saiyagym

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
class Option3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_option3, container, false)

        // Encuentra el botón de cerrar sesión en el diseño inflado
        val closeButton = view.findViewById<MaterialButton>(R.id.CerrarSesion)

        // Agrega un oyente de clic al botón
        closeButton.setOnClickListener {
            // Obtener las SharedPreferences desde la actividad asociada al fragmento
            val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

            // Aquí puedes agregar el código para cerrar la sesión del usuario
            // Por ejemplo, puedes limpiar las SharedPreferences
            val editor = sharedPreferences.edit()
            editor.clear().apply()
            requireActivity().finish()
            // Luego, puedes realizar alguna acción, como iniciar una actividad de inicio de sesión
        }

        return view
    }
}
