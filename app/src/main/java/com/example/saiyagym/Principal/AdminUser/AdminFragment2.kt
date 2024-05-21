package com.example.saiyagym.Principal.AdminUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.saiyagym.LogHelper
import com.example.saiyagym.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
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
        val spinnerCategory: Spinner = view.findViewById(R.id.spinnerCategory)
        val spinnerDay: Spinner = view.findViewById(R.id.spinnerDay)
        val editTextID: EditText = view.findViewById(R.id.editTextID)
        val editTextName: EditText = view.findViewById(R.id.editTextName)
        val editTextDescription: EditText = view.findViewById(R.id.editTextDescription)
        val editTextVideoURL: EditText = view.findViewById(R.id.editTextVideoURL)
        val buttonSubmit: Button = view.findViewById(R.id.buttonSubmit)
        val spinnerDeleteCategory: Spinner = view.findViewById(R.id.spinnerDeleteCategory)
        val spinnerDeleteDay: Spinner = view.findViewById(R.id.spinnerDeleteDay)
        val editTextDeleteExerciseName: EditText = view.findViewById(R.id.editTextDeleteExerciseName)
        val buttonDeleteExercise: Button = view.findViewById(R.id.buttonDeleteExercise)

        buttonDeleteExercise.setOnClickListener {
            val categoryIndex = spinnerDeleteCategory.selectedItemPosition
            val dayIndex = spinnerDeleteDay.selectedItemPosition
            val exerciseName = editTextDeleteExerciseName.text.toString()

            val category = when (categoryIndex) {
                0 -> "1"
                1 -> "0"
                2 -> "Mantenimiento"
                3 -> "Definición"
                else -> "Unknown"
            }

            val day = when (dayIndex) {
                0 -> "0" // lunes
                1 -> "1" // martes
                2 -> "2" // miércoles
                3 -> "3" // jueves
                4 -> "4" // viernes
                5 -> "5" // sábado
                6 -> "6" // domingo
                else -> "Unknown"
            }

            deleteExerciseFromFirebase(category, day, exerciseName)
        }

        buttonSubmit.setOnClickListener {
            val categoryIndex = spinnerCategory.selectedItemPosition
            val dayIndex = spinnerDay.selectedItemPosition
            val id = editTextID.text.toString()
            val name = editTextName.text.toString()
            val description = editTextDescription.text.toString()
            val videoURL = editTextVideoURL.text.toString()
            val category = when (categoryIndex) {
                0 -> "1"
                1 -> "0"
                2 -> "Mantenimiento"
                3 -> "Definición"
                else -> "Unknown"
            }
            val day = when (dayIndex) {
                0 -> "0"//lunes
                1 -> "1"//martes
                2 -> "2"//miercoles
                3 -> "3"//jueves
                4 -> "4"//viernes
                5 -> "5"//sabado
                6 -> "6"//domingo
                else -> "Unknown"
            }
            val exercise = Exercise(description, id, name, videoURL)
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


        exerciseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exerciseCount = dataSnapshot.childrenCount.toInt()

                val exerciseId = (exerciseCount).toString()

                val ejercicioMap = mapOf(
                    "Nombre" to exercise.Nombre,
                    "VideoURL" to exercise.VideoURL,
                    "Descripcion" to exercise.Descripcion,
                    "ID" to exerciseId
                )

                val exerciseRefWithId = exerciseRef.child(exerciseId)

                exerciseRefWithId.setValue(ejercicioMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        LogHelper.saveChangeLog(requireContext(), "Guardar ejercicio", "INFO")
                        val snackbar = Snackbar.make(requireView(), "Ejercicio guardado con exito", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    } else {
                        LogHelper.saveChangeLog(
                            requireContext(),
                            "Error al guardar ejercicio",
                            "ERROR"
                        )
                        val snackbar = Snackbar.make(requireView(), "Error al guardar ejercicio", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    data class Exercise(
        val Descripcion: String,
        val ID: String,
        val Nombre: String,
        val VideoURL: String
    )

    private fun deleteExerciseFromFirebase(category: String, day: String, exerciseName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val exercisesRef = database.child("Categorias")
            .child(category)
            .child("PlanDeEjercicios")
            .child(day)
            .child("Ejercicios")

        exercisesRef.orderByChild("Nombre").equalTo(exerciseName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (exerciseSnapshot in dataSnapshot.children) {
                            exerciseSnapshot.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (user != null) {
                                            LogHelper.saveChangeLog(
                                                requireContext(),
                                                "Ejercicio Eliminado",
                                                "INFO"
                                            )
                                        val snackbar = Snackbar.make(requireView(), "Eliminado con exito", Snackbar.LENGTH_SHORT)
                                        snackbar.show()
                                        }
                                    } else {
                                        LogHelper.saveChangeLog(
                                            requireContext(),
                                            "Error al eliminar ejercicio",
                                            "ERROR"
                                        )
                                        val snackbar = Snackbar.make(requireView(), "Error al eliminar el ejercicio", Snackbar.LENGTH_SHORT)
                                        snackbar.show()
                                    }
                                }
                        }
                    } else {
                        LogHelper.saveChangeLog(
                            requireContext(),
                            "Ejercicio no encontrado",
                            "ERROR"
                        )
                        val snackbar = Snackbar.make(requireView(), "No se ha encontrado el ejercicio", Snackbar.LENGTH_SHORT)
                        snackbar.show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    val snackbar = Snackbar.make(requireView(), "Error en la base de dato", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            })
    }

}
