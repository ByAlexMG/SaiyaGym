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
            showPopup(holder.itemView.context, day)
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

    private fun showPopup(context: Context, day: String) {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_exercise, null)

        val popupTextView: TextView = view.findViewById(R.id.textViewExercise)
        val popupButton: Button = view.findViewById(R.id.buttonClose)

        popupTextView.text = "Detalles del día: $day"

        val alertDialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        popupButton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}
