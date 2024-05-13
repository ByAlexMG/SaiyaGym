package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Calendar
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.saiyagym.databinding.FragmentOption2Binding // Importa tu clase de enlace de vistas

class Option2Fragment : Fragment() {
    private lateinit var binding: FragmentOption2Binding // Declara tu variable de enlace de vistas
    private lateinit var recyclerViewOption2: RecyclerView
    private val diasSemana = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla el diseño utilizando el objeto de enlace de vistas
        binding = FragmentOption2Binding.inflate(inflater, container, false)
        val view = binding.root

        recyclerViewOption2 = view.findViewById(R.id.recyclerViewOption2)
        recyclerViewOption2.layoutManager = LinearLayoutManager(requireContext())

        val dayOfWeek = getDayOfWeek()
        getExerciseNames(dayOfWeek)

        return view
    }

    private fun getDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Lunes"
            Calendar.TUESDAY -> "Martes"
            Calendar.WEDNESDAY -> "Miércoles"
            Calendar.THURSDAY -> "Jueves"
            Calendar.FRIDAY -> "Viernes"
            Calendar.SATURDAY -> "Sábado"
            else -> throw IllegalStateException("Error al obtener el día de la semana")
        }
    }

    private fun getExerciseNames(dayOfWeek: String) {
        lifecycleScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let { uid ->
                    val jsonString =
                        fetchJsonStringFromUrl("https://saiyagym-9000-default-rtdb.firebaseio.com/Categorias/0.json")
                    val jsonObject = JSONObject(jsonString)
                    val planDeEjerciciosArray = jsonObject.getJSONArray("PlanDeEjercicios")

                    for (i in 0 until planDeEjerciciosArray.length()) {
                        val planDeEjerciciosObject = planDeEjerciciosArray.getJSONObject(i)
                        val diaSemana = planDeEjerciciosObject.getString("DiaSemana")
                        if (diaSemana == dayOfWeek) {
                            val ejerciciosArray = planDeEjerciciosObject.getJSONArray("Ejercicios")
                            val exerciseNames = mutableListOf<String>()
                            for (j in 0 until ejerciciosArray.length()) {
                                val ejercicioObject = ejerciciosArray.getJSONObject(j)
                                val nombre = ejercicioObject.getString("Nombre")
                                val ID = ejercicioObject.getString("ID")

                                // Guardar en Firestore
                                saveExerciseToFirestore(uid, dayOfWeek, nombre, ID)

                                exerciseNames.add(nombre)
                            }
                            // Update UI here
                            recyclerViewOption2.adapter = CustomAdapter(exerciseNames)
                            break // No need to continue looping once we found exercise names
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    private fun saveExerciseToFirestore(userId: String, dayOfWeek: String, exerciseName: String, exerciseId: String) {
        val db = FirebaseFirestore.getInstance()
        val exercisesCollection = db.collection("users").document(userId)
            .collection("Exercises").document(dayOfWeek)
            .collection("ExerciseList").document(exerciseId)
        val exerciseData = hashMapOf(
            "name" to exerciseName,
            "id" to exerciseId
        )
        exercisesCollection.set(exerciseData)
            .addOnSuccessListener {
                // Éxito al guardar el ejercicio en Firestore
            }
            .addOnFailureListener { exception ->
                // Manejar error
            }
    }
    private suspend fun fetchJsonStringFromUrl(urlString: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            try {
                val reader = connection.inputStream.bufferedReader()
                val jsonString = reader.use { it.readText() }
                jsonString
            } finally {
                connection.disconnect()
            }
        }
    }


}
