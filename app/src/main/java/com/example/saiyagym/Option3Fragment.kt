package com.example.saiyagym

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
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
        val principal = activity as Principal
        val sp = principal.getSharedPreferences("my_preferences", MODE_PRIVATE)
        val editor = sp.edit()
        val switch: Switch = view.findViewById(R.id.switchTema)

        val theme = sp.getInt("theme", 1)
        if(theme==1){
            switch.setChecked( false)
        }else{
            switch.setChecked( true)
        }
        switch.setOnClickListener {
            if (switch.isChecked) {
                editor.putInt("theme", 0)
            } else {
                editor.putInt("theme", 1)
            }
            editor.apply()
            principal.setDayNight()
        }



        return view
    }
}
