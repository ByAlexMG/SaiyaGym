package com.example.saiyagym

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private val days: List<String>, private val categoria: String) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        holder.dayTextView.text = day

        val imageResId = getImageResIdForDay(categoria, position % 7)
        holder.imageView.setImageResource(imageResId)
        holder.imageView.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int = days.size

    private fun getImageResIdForDay(categoria: String, dayIndex: Int): Int {
        return when (categoria) {
            "cardio" -> {
                when (dayIndex) {
                    0 -> R.drawable.nadar
                    1 -> R.drawable.nadar
                    2 -> R.drawable.nadar
                    3 -> R.drawable.nadar
                    4 -> R.drawable.nadar
                    5 -> R.drawable.nadar
                    else -> R.drawable.nadar
                }
            }
            "volumen" -> {
                when (dayIndex) {
                    0 -> R.drawable.nadar
                    1 -> R.drawable.nadar
                    2 -> R.drawable.nadar
                    3 -> R.drawable.nadar
                    4 -> R.drawable.nadar
                    5 -> R.drawable.nadar
                    else -> R.drawable.nadar
                }
            }
            "definicion" -> {
                when (dayIndex) {
                    0 -> R.drawable.nadar
                    1 -> R.drawable.nadar
                    2 -> R.drawable.nadar
                    3 -> R.drawable.nadar
                    4 -> R.drawable.nadar
                    5 -> R.drawable.nadar
                    else -> R.drawable.nadar
                }
            }
            "mantenimiento" -> {
                when (dayIndex) {
                    0 -> R.drawable.ajustes
                    1 -> R.drawable.add
                    2 -> R.drawable.admin
                    3 -> R.drawable.nadar
                    4 -> R.drawable.nadar
                    5 -> R.drawable.calendario
                    else -> R.drawable.calendario
                }
            }
            else -> R.drawable.ajustes
        }
    }
}
