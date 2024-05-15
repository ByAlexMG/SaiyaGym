package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminFragment2 : Fragment() {

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin2, container, false)

        database = FirebaseDatabase.getInstance().reference

        // Get references to UI elements
        val spinnerCategory: Spinner = view.findViewById(R.id.spinnerCategory)
        val spinnerDay: Spinner = view.findViewById(R.id.spinnerDay)
        val editTextID: EditText = view.findViewById(R.id.editTextID)
        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextDescription: EditText = view.findViewById(R.id.editTextDescription)
        val editTextVideoURL: EditText = view.findViewById(R.id.editTextVideoURL)
        val buttonSubmit: Button = view.findViewById(R.id.buttonSubmit)

        // Set up the button click listener
        buttonSubmit.setOnClickListener {
            val categoryIndex = spinnerCategory.selectedItemPosition
            val dayIndex = spinnerDay.selectedItemPosition
            val id = editTextID.text.toString()
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val videoURL = editTextVideoURL.text.toString()

            // Map category index to category name
            val category = when (categoryIndex) {
                0 -> "1"
                1 -> "0"
                2 -> "Mantenimiento"
                3 -> "Definición"
                else -> "Unknown" // Default case, should not happen if spinner is set up correctly
            }

            // Map day index to day name
            val day = when (dayIndex) {
                0 -> "0" // Lunes
                1 -> "1" // Martes
                2 -> "2" // Miércoles
                3 -> "3" // Jueves
                4 -> "4" // Viernes
                5 -> "5" // Sábado
                6 -> "6" // Domingo
                else -> "Unknown" // Default case, should not happen if spinner is set up correctly
            }

            // Create Exercise object
            val exercise = Exercise(description, id, name, videoURL)

            // Save to Firebase
            saveExerciseToFirebase(category, day, exercise)
        }

        return view
    }

    private fun saveExerciseToFirebase(category: String, day: String, exercise: Exercise) {
        val exerciseRef = database.child("Categorias")
            .child(category)
            .child("PlanDeEjercicios")
            .child(day)
            .child("Ejercicios")

        // Obtener el número actual de ejercicios
        exerciseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exerciseCount = dataSnapshot.childrenCount.toInt()

                // El nuevo ID será el siguiente número después del recuento actual
                val exerciseId = (exerciseCount).toString()

                // Convertir los nombres de los campos a mayúsculas
                val ejercicioMap = mapOf(
                    "Nombre" to exercise.Nombre,
                    "VideoURL" to exercise.VideoURL,
                    "Descripcion" to exercise.Descripcion,
                    "ID" to exerciseId
                )

                val exerciseRefWithId = exerciseRef.child(exerciseId)

                // Guardar el nuevo ejercicio en Firebase
                exerciseRefWithId.setValue(ejercicioMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Éxito: el ejercicio se ha guardado correctamente
                    } else {
                        // Error al guardar el ejercicio
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Manejar errores de Firebase
            }
        })
    }




    data class Exercise(
        val Descripcion: String,
        val ID: String,
        val Nombre: String,
        val VideoURL: String
    )
}
