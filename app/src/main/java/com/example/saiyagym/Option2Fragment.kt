package com.example.saiyagym
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Option2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_option2, container, false)


        val recyclerViewOption2: RecyclerView = view.findViewById(R.id.recyclerViewOption2)


        recyclerViewOption2.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewOption2.adapter = CustomAdapter(5)
        return view
    }
}
