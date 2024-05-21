package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class Option1Fragment : Fragment() {

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_option1, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        db = FirebaseFirestore.getInstance()

        fetchCategory { category ->
            val days = listOf(
                "L", "M", "X", "J", "V", "S", "D"
            )
            val adapter = CalendarAdapter(days, category, today)
            recyclerView.adapter = adapter
        }

        return view
    }

    private fun fetchCategory(callback: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val uid = currentUser.uid
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val category = document.getString("categoria") ?: "default"
                        callback(category)
                    }
                }
        }
    }
}
