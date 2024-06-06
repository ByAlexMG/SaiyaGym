package com.example.saiyagym.principal.userFragments.Fragment1

import android.app.AlertDialog
import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
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
                    4 -> R.drawable.baile
                    5 -> R.drawable.chill
                    else -> R.drawable.chill
                }
            }
            else -> R.drawable.ajustes
        }
    }
    private fun getPopupImageResId(categoria: String, dayIndex: Int): Int {
        return when (categoria) {

            "cardio" -> {
                when (dayIndex % 7) {
                    0 -> R.drawable.gokucarrera
                    1 -> R.drawable.bicitruco
                    2 -> R.drawable.nadatruco
                    3 -> R.drawable.bailegif
                    4 -> R.drawable.boxeotruco
                    5 -> R.drawable.descanso
                    else -> R.drawable.descanso
                }
            }
            "volumen", "definicion", "mantenimiento" -> {
                when (dayIndex) {
                    0 -> R.drawable.biceps
                    1 -> R.drawable.pecho
                    2 -> R.drawable.abdominales
                    3 -> R.drawable.piernas_br
                    4 -> R.drawable.brazos
                    5 -> R.drawable.descanso
                    else -> R.drawable.descanso
                }
            }
            else -> R.drawable.ajustes
        }
    }
    private fun getPopupText(context: Context,categoria: String, day: String, dayIndex: Int): String {
        return when (categoria) {
            "cardio" -> {
                when (dayIndex % 7) {
                    0 -> context.getString(R.string.day_0)
                    1 -> context.getString(R.string.day_1)
                    2 -> context.getString(R.string.day_2)
                    3 -> context.getString(R.string.day_3)
                    4 -> context.getString(R.string.day_4)
                    5 -> context.getString(R.string.rest_day)
                    else -> context.getString(R.string.rest_day)
                }
            }
            "volumen", "definicion", "mantenimiento" -> {
                when (dayIndex % 7) {
                    0 -> context.getString(R.string.day_en_0)
                    1 -> context.getString(R.string.day_en_1)
                    2 -> context.getString(R.string.day_en_2)
                    3 -> context.getString(R.string.day_en_3)
                    4 -> context.getString(R.string.day_en_4)
                    5 -> context.getString(R.string.day_en_rest)
                    else -> context.getString(R.string.day_en_rest)
                }
            }
            else -> "Error al cargar datos"
        }
    }

    private fun showPopup(context: Context, day: String, dayIndex: Int) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_exercise, null)

        val popupImageView: ImageView = view.findViewById(R.id.imageViewExercise)
        val popupTextView: TextView = view.findViewById(R.id.textViewExercise)
        val popupButton: Button = view.findViewById(R.id.buttonClose)

        val popupImageResId = getPopupImageResId(categoria, dayIndex)
        popupImageView.setImageResource(popupImageResId)

        val popupText = getPopupText(context,categoria, day, dayIndex)
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
