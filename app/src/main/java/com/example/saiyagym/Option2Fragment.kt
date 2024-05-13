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
import org.json.JSONArray

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

        getExerciseNames()

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

    private fun getExerciseNames() {
        lifecycleScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let { uid ->
                    val jsonString =
                        fetchJsonStringFromUrl("https://saiyagym-9000-default-rtdb.firebaseio.com/Categorias/0.json")
                    val jsonObject = JSONObject(jsonString)
                    val planDeEjerciciosArray = jsonObject.getJSONArray("PlanDeEjercicios")

                    val dayOfWeek = getDayOfWeek()
                    val exerciseNames = mutableListOf<String>() // Lista para almacenar los ejercicios del día actual

                    for (i in 0 until planDeEjerciciosArray.length()) {
                        val planDeEjerciciosObject = planDeEjerciciosArray.getJSONObject(i)
                        val diaSemana = planDeEjerciciosObject.getString("DiaSemana")
                        val ejerciciosArray = planDeEjerciciosObject.getJSONArray("Ejercicios")

                        // Guardar los ejercicios para todos los días en la base de datos
                        saveExercisesForDay(uid, diaSemana, ejerciciosArray)

                        // Si el día de la semana obtenido coincide con el día actual, agregar los ejercicios a la lista
                        if (diaSemana == dayOfWeek) {
                            for (j in 0 until ejerciciosArray.length()) {
                                val ejercicioObject = ejerciciosArray.getJSONObject(j)
                                val nombre = ejercicioObject.getString("Nombre")
                                exerciseNames.add(nombre)
                            }
                        }
                    }

                    // Mostrar solo los ejercicios del día actual en la pantalla
                    recyclerViewOption2.adapter = CustomAdapter(exerciseNames)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    private fun saveExercisesForDay(userId: String, dayOfWeek: String, exercisesArray: JSONArray) {
        val db = FirebaseFirestore.getInstance()
        val exercisesCollection = db.collection("users").document(userId)
            .collection("Exercises").document(dayOfWeek)
            .collection("ExerciseList")

        for (j in 0 until exercisesArray.length()) {
            val ejercicioObject = exercisesArray.getJSONObject(j)
            val nombre = ejercicioObject.getString("Nombre")
            val ID = ejercicioObject.getString("ID")

            // Guardar el ejercicio en Firestore con el nombre como clave y el ID como valor
            val exerciseData = hashMapOf(
                "name" to nombre,
                "id" to ID
            )
            exercisesCollection.document(ID).set(exerciseData)
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
