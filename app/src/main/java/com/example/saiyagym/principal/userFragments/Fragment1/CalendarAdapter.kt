package com.example.saiyagym.principal.userFragments.Fragment1

import android.app.AlertDialog
import android.content.Context
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
    private fun getPopupText(categoria: String, day: String, dayIndex: Int): String {
        return when (categoria) {
            "cardio" -> {
                when (dayIndex % 7) {
                    0 -> "Hoy se realizarán ejercicios cardiovaculares de una intensidad moderada para comenzar a reducir las reservas de glocógeno que hemos llenado durante el fin de semana."
                    1 -> "Realizaremos ejercicios con intervalos de alta intensidad para comenzar a adaptar el cuerpo a esfuerzos con intensidad moderada-intensa."
                    2 -> "Practicaremos ejercicios multiarticulares para favorecer la adaptación del cuerpoa  ciertos movimientos."
                    3 -> "Hoy se reealizarán ejercicios en los que se priorizará el entetenimiento del propio usuario para ahcer mas ameno el entretenimiento."
                    4 -> "Se realizarán ejercicios con un poco de mas imacto articular per que ayudarána  despegar la grasa del tejido adiposo con mayor facilidad."
                    5 -> "Descanso"
                    else -> "Descanso"
                }
            }
            "volumen" -> {
                when (dayIndex % 7) {
                    0 -> "Comenzaremos la semana con ejercicios centrados en fortalecer al musculatura de la espalda y como musuclo secundario nos centraremos en el biceps."
                    1 -> "Continuaremos la semana con ejercicios centrados en fortalecer al musculatura del pecho y como musuclo secundario nos centraremos en el triceps."
                    2 -> "Toca realizar ejercicios apra fortalecer y desarrollar la pared abdominal, debes tener en cuenta de que par marcar abdominales, lo primero que debes hacer es reducir tu % de grasa corporal para poder sacarlos a lucir."
                    3 -> "Hoy toca el dia mas temido por todos los usuarios de gym, ya que a ninguna persona le gusta hacer piernas, eso si... preparate para las agujetas en 2 días."
                    4 -> "Hoy se van a realizar ejercicios para fortalecer las extremidades superiores sobre todo la parte de biceps y triceps, ya que han pasado las 72 horas necesarias para volver a entrenar musculos con fibras de contraccion rápida."
                    5 -> "Descanso"
                    else -> "Descanso"
                }
            }
            "definicion" -> {
                when (dayIndex % 7) {
                    0 -> "Comenzaremos la semana con ejercicios centrados en fortalecer al musculatura de la espalda y como musuclo secundario nos centraremos en el biceps."
                    1 -> "Continuaremos la semana con ejercicios centrados en fortalecer al musculatura del pecho y como musuclo secundario nos centraremos en el triceps."
                    2 -> "Toca realizar ejercicios apra fortalecer y desarrollar la pared abdominal, debes tener en cuenta de que par marcar abdominales, lo primero que debes hacer es reducir tu % de grasa corporal para poder sacarlos a lucir."
                    3 -> "Hoy toca el dia mas temido por todos los usuarios de gym, ya que a ninguna persona le gusta hacer piernas, eso si... preparate para las agujetas en 2 días."
                    4 -> "Hoy se van a realizar ejercicios para fortalecer las extremidades superiores sobre todo la parte de biceps y triceps, ya que han pasado las 72 horas necesarias para volver a entrenar musculos con fibras de contraccion rápida."
                    5 -> "Descanso"
                    else -> "Descanso"
                }
            }
            "mantenimiento" -> {
                when (dayIndex % 7) {
                    0 -> "Comenzaremos la semana con ejercicios centrados en fortalecer al musculatura de la espalda y como musuclo secundario nos centraremos en el biceps."
                    1 -> "Continuaremos la semana con ejercicios centrados en fortalecer al musculatura del pecho y como musuclo secundario nos centraremos en el triceps."
                    2 -> "Toca realizar ejercicios apra fortalecer y desarrollar la pared abdominal, debes tener en cuenta de que par marcar abdominales, lo primero que debes hacer es reducir tu % de grasa corporal para poder sacarlos a lucir."
                    3 -> "Hoy toca el dia mas temido por todos los usuarios de gym, ya que a ninguna persona le gusta hacer piernas, eso si... preparate para las agujetas en 2 días."
                    4 -> "Hoy se van a realizar ejercicios para fortalecer las extremidades superiores sobre todo la parte de biceps y triceps, ya que han pasado las 72 horas necesarias para volver a entrenar musculos con fibras de contraccion rápida."
                    5 -> "Descanso"
                    else -> "Descanso"
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
