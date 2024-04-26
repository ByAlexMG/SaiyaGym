package com.example.saiyagym

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Option1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_option1, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.calendarRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 7)


        val days = listOf("L", "M", "X", "J", "V", "S", "D",
                          "L", "M", "X", "J", "V", "S", "D",
                          "L", "M", "X", "J", "V", "S", "D",
                          "L", "M", "X", "J", "V", "S", "D")

        val adapter = CalendarAdapter(days)
        recyclerView.adapter = adapter

        return view
    }
}
