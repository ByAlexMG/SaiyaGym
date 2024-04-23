package com.example.saiyagym

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class Principal : AppCompatActivity() {

    private var expanded = false
    private lateinit var initialParams: ViewGroup.LayoutParams
    private var originalHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.principal)
        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtubePlayerView)
        val cardView = findViewById<CardView>(R.id.cardView)
        val playButton = findViewById<Button>(R.id.playButton)
        initialParams = cardView.layoutParams
        originalHeight = initialParams.height

        cardView.setOnClickListener {
            if (!expanded) {
                expandCardView(cardView, youTubePlayerView)
            }
        }

        playButton.setOnClickListener{
            collapseCardView(cardView, youTubePlayerView)
        }

        // Inicializa el reproductor de YouTube

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {

                youTubePlayer.loadVideo("TXvwdNM_43I", 0F)
            }
        })
    }

    private fun expandCardView(cardView: CardView, youTubePlayerView: YouTubePlayerView) {
        val expandAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.expand_anim)
        cardView.startAnimation(expandAnimation)
        cardView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        cardView.findViewById<View>(R.id.youtubePlayerView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.playButton).visibility = View.VISIBLE

        youTubePlayerView.visibility = View.VISIBLE
        expanded = true
    }


    private fun collapseCardView(cardView: CardView, youTubePlayerView: YouTubePlayerView) {
        val collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.collapse_anim)
        cardView.startAnimation(collapseAnimation)
        cardView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = originalHeight
        }
        cardView.findViewById<View>(R.id.youtubePlayerView).visibility = View.GONE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.GONE
        cardView.findViewById<View>(R.id.playButton).visibility = View.GONE
        expanded = false


        youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.pause()
            }
        })
    }


}
