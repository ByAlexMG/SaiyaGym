package com.example.saiyagym

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Calendar
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.saiyagym.databinding.FragmentOption2Binding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray

class Option2Fragment : Fragment() {
    private lateinit var binding: FragmentOption2Binding
    private lateinit var recyclerViewOption2: RecyclerView

    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOption2Binding.inflate(inflater, container, false)
        val view = binding.root
        progressBar = view.findViewById(R.id.progressBar)

        // Mostrar ProgressBar mientras se cargan los datos
        progressBar.visibility = View.VISIBLE

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

                    val exerciseList = mutableListOf<Exercise>()

                    for (i in 0 until planDeEjerciciosArray.length()) {
                        val planDeEjerciciosObject = planDeEjerciciosArray.getJSONObject(i)
                        val diaSemana = planDeEjerciciosObject.getString("DiaSemana")
                        val ejerciciosArray = planDeEjerciciosObject.getJSONArray("Ejercicios")
                        saveExercisesForDay(uid, diaSemana, ejerciciosArray)
                        if (diaSemana == dayOfWeek) {
                            for (j in 0 until ejerciciosArray.length()) {
                                val ejercicioObject = ejerciciosArray.getJSONObject(j)
                                val nombre = ejercicioObject.getString("Nombre")
                                val videoURL = ejercicioObject.getString("VideoURL")
                                val descripcion = ejercicioObject.getString("Descripcion")
                                val exercise = Exercise(nombre, videoURL, descripcion)
                                exerciseList.add(exercise)
                            }
                        }
                    }
                    recyclerViewOption2.adapter = CustomAdapter(exerciseList)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                LogHelper.saveChangeLog(requireContext(), "Error al cargar ejercicios", "ERROR")
                val snackbar = Snackbar.make(requireView(), "Error al cargar ejercicios", Snackbar.LENGTH_SHORT)
                snackbar.show()  }
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
            val id = ejercicioObject.getString("ID")
            val repes = ejercicioObject.getString("Descripcion")
            val videoURL = ejercicioObject.getString("VideoURL")

            val exerciseData = hashMapOf(
                "name" to nombre,
                "id" to id,
                "repes" to repes,
                "videoURL" to videoURL
            )
            exercisesCollection.document(id).set(exerciseData)
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