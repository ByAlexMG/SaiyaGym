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

        val view = inflater.inflate(R.layout.fragment_option3, container, false)


        val closeButton = view.findViewById<MaterialButton>(R.id.CerrarSesion)


        closeButton.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)


            val editor = sharedPreferences.edit()
            editor.clear().apply()
            requireActivity().finish()
           }

        return view
    }
}
