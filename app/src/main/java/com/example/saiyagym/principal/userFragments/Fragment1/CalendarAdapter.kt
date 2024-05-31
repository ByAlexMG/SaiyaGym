package com.example.saiyagym.principal.userFragments.Fragment1

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saiyagym.R

class CalendarAdapter(
    private val days: List<String>,
    private val categoria: String,
    private val today: Int
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
        val imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
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
        holder.imageButton.setImageResource(imageResId)
        holder.imageButton.setBackgroundResource(android.R.color.transparent)
        holder.imageButton.visibility = View.VISIBLE

        if ((position + 1) == (today - 1)) {
            holder.itemView.setBackgroundResource(R.drawable.border_highlight)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.transparent)
        }

        holder.imageButton.setOnClickListener {
            showPopup(holder.itemView.context, day, position)
        }
    }

    override fun getItemCount(): Int = days.size

    private fun getImageResIdForDay(categoria: String, dayIndex: Int): Int {
        return when (categoria) {
            "cardio" -> {
                when (dayIndex) {
                    0 -> R.drawable.correr
                    1 -> R.drawable.bici
                    2 -> R.drawable.nadar
                    3 -> R.drawable.baile
                    4 -> R.drawable.boxing
                    5 -> R.drawable.chill
                    else -> R.drawable.chill
                }
            }
            "volumen", "definicion", "mantenimiento" -> {
                when (dayIndex) {
                    0 -> R.drawable.ejercicios_logo
                    1 -> R.drawable.press
                    2 -> R.drawable.abs
                    3 -> R.drawable.piernas
                    4 -> R.drawable.arm
                    5 -> R.drawable.chill
                    else -> R.drawable.chill
                }
            }
            else -> R.drawable.ajustes
        }
    }

    private fun getPopupText(categoria: String, day: String, dayIndex: Int): String {
        return when (categoria) {
            "cardio" -> {
                when (dayIndex % 7) {
                    0 -> "Detalles de correr para el día $day"
                    1 -> "Detalles de bici para el día $day"
                    2 -> "Detalles de nadar para el día $day"
                    3 -> "Detalles de baile para el día $day"
                    4 -> "Detalles de boxing para el día $day"
                    5 -> "Detalles de chill para el día $day"
                    else -> "Detalles de chill para el día $day"
                }
            }
            "volumen" -> {
                when (dayIndex % 7) {
                    0 -> "Detalles de ejercicios para el día $day"
                    1 -> "Detalles de press para el día $day"
                    2 -> "Detalles de abs para el día $day"
                    3 -> "Detalles de piernas para el día $day"
                    4 -> "Hoy se van a realizar ejercicios apra fortalecer las extremidades superiores sobre todo la aprte de biceps y triceps, ya que han paado als 72 horas encesarias para volver a entrenar musculos grandes  "
                    5 -> "Detalles de chill para el día $day"
                    else -> "Detalles de chill para el día $day"
                }
            }
            "definicion" -> {
                when (dayIndex % 7) {
                    0 -> "Detalles de ejercicios de definición para el día $day"
                    1 -> "Detalles de press de definición para el día $day"
                    2 -> "Detalles de abs de definición para el día $day"
                    3 -> "Detalles de piernas de definición para el día $day"
                    4 -> "Detalles de arm de definición para el día $day"
                    5 -> "Detalles de chill para el día $day"
                    else -> "Detalles de chill para el día $day"
                }
            }
            "mantenimiento" -> {
                when (dayIndex % 7) {
                    0 -> "Detalles de ejercicios de mantenimiento para el día $day"
                    1 -> "Detalles de press de mantenimiento para el día $day"
                    2 -> "Detalles de abs de mantenimiento para el día $day"
                    3 -> "Detalles de piernas de mantenimiento para el día $day"
                    4 -> "Detalles de arm de mantenimiento para el día $day"
                    5 -> "Detalles de chill para el día $day"
                    else -> "Detalles de chill para el día $day"
                }
            }
            else -> "Detalles del día $day"
        }
    }

    private fun showPopup(context: Context, day: String, dayIndex: Int) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_exercise, null)

        val popupTextView: TextView = view.findViewById(R.id.textViewExercise)
        val popupButton: Button = view.findViewById(R.id.buttonClose)

        val popupText = getPopupText(categoria, day, dayIndex)
        popupTextView.text = popupText

        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        popupButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
