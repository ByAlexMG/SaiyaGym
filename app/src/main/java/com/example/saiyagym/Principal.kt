package com.example.saiyagym
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.VideoView
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
                restoreCardView(cardView)
            }
        }
    }

    private fun expandCardView(cardView: CardView) {
        cardView.layoutParams.apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        cardView.findViewById<VideoView>(R.id.videoView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.VISIBLE
        cardView.findViewById<Button>(R.id.playButton).visibility = View.VISIBLE

        // Configurar el VideoView para reproducir un video local
        val videoView = cardView.findViewById<VideoView>(R.id.videoView)
        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.Video)
        videoView.setVideoURI(videoUri)
        videoView.start()

        expanded = true
    }

    private fun restoreCardView(cardView: CardView) {
        cardView.layoutParams = initialParams
        cardView.findViewById<VideoView>(R.id.videoView).visibility = View.GONE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.GONE
        cardView.findViewById<Button>(R.id.playButton).visibility = View.GONE
        expanded = false
    }
}

