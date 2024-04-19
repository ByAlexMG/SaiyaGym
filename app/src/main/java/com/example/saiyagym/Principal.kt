package com.example.saiyagym


import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Principal : AppCompatActivity() {

    private var expanded = false
    private lateinit var initialParams: ViewGroup.LayoutParams
    private var originalHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)


        

        val cardView = findViewById<CardView>(R.id.cardView)
        initialParams = cardView.layoutParams
        originalHeight = initialParams.height

        cardView.setOnClickListener {
            if (!expanded) {
                expandCardView(cardView)
            } else {
                collapseCardView(cardView)
            }
        }
    }

    private fun expandCardView(cardView: CardView) {
        val expandAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.expand_anim)
        cardView.startAnimation(expandAnimation)
        cardView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        cardView.findViewById<View>(R.id.videoView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.playButton).visibility = View.VISIBLE
        expanded = true
    }

    private fun restoreCardView(cardView: CardView) {
        cardView.layoutParams = initialParams
        cardView.findViewById<View>(R.id.videoView).visibility = View.GONE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.GONE
        cardView.findViewById<View>(R.id.playButton).visibility = View.GONE
        expanded = false
    }
    private fun collapseCardView(cardView: CardView) {
        val collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.collapse_anim)
        cardView.startAnimation(collapseAnimation)
        cardView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = originalHeight // Restablecer la altura original
        }
        cardView.findViewById<View>(R.id.videoView).visibility = View.GONE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.GONE
        cardView.findViewById<View>(R.id.playButton).visibility = View.GONE
        expanded = false
    }
}
