package com.example.saiyagym
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.cardview.widget.CardView

import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.lang.Integer.max
class CustomAdapter(private val itemCount: Int) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val originalHeights = mutableMapOf<Int, Int>() // Map para almacenar las alturas originales de cada CardView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cardView.setOnClickListener {
            expandCardView(holder.cardView, holder.youTubePlayerView, holder.itemView, holder)
        }
        holder.playButton.setOnClickListener {
            collapseCardView(holder.cardView, holder.youTubePlayerView, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
        return itemCount
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var expanded = false
        val youTubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val playButton: Button = itemView.findViewById(R.id.playButton)
    }

    private fun expandCardView(cardView: CardView, youTubePlayerView: YouTubePlayerView, itemView: View, holder: ViewHolder) {
        // Guardar la altura original antes de expandir (si aún no está guardada)
        originalHeights[cardView.hashCode()] = originalHeights[cardView.hashCode()] ?: cardView.height

        // Obtener la altura deseada para la expansión
        cardView.measure(View.MeasureSpec.makeMeasureSpec(cardView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val targetHeight = cardView.measuredHeight

        // Crear una animación de expansión con la altura adecuada
        val expandAnimation = ValueAnimator.ofInt(cardView.height, targetHeight)
        expandAnimation.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            cardView.layoutParams.height = animatedValue
            cardView.requestLayout()
        }
        expandAnimation.duration = 300
        expandAnimation.start()

        // Mostrar los elementos ocultos
        cardView.findViewById<View>(R.id.youtubePlayerView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.playButton).visibility = View.VISIBLE
    }
    private fun collapseCardView(cardView: CardView, youTubePlayerView: YouTubePlayerView, itemView: View) {
        val originalHeight = originalHeights[cardView.hashCode()] ?: ViewGroup.LayoutParams.WRAP_CONTENT

        val collapseAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.collapse_anim)
        collapseAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Restablecer la altura original después del colapso
                cardView.layoutParams.height = originalHeight
                cardView.requestLayout()
            }
        })

        cardView.startAnimation(collapseAnimation)

        // Guardar la altura original antes de colapsar (si aún no está guardada)
        originalHeights[cardView.hashCode()] = originalHeights[cardView.hashCode()] ?: cardView.height

        val initialHeight = cardView.height
        val animation = object : Animation() {
            override fun applyTransformation(
                interpolatedTime: Float,
                t: android.view.animation.Transformation?
            ) {
                val newHeight = (initialHeight * (1 - interpolatedTime)).toInt()
                // Limitar la nueva altura para que no sea menor que la altura original
                cardView.layoutParams.height = max(newHeight, originalHeight)
                cardView.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = 500 // Duración de la animación en milisegundos
        cardView.startAnimation(animation)
    }


}