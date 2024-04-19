package com.example.saiyagym
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val itemCount: Int) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Puedes agregar aquí la lógica para mostrar datos específicos en cada elemento si es necesario
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Aquí puedes acceder a los elementos de tu cardview_item.xml si es necesario
    }
}
