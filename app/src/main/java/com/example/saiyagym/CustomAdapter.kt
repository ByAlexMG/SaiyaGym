package com.example.saiyagym
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.cardview.widget.CardView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import java.lang.Integer.max
class CustomAdapter(private val exercises: List<Exercise>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private val originalHeights = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return exercises.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.exerciseNameTextView.text = exercise.name
        holder.descriptionTextView.text = exercise.description

        holder.cardView.setOnClickListener {
            expandCardView(holder.cardView)
            holder.youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.loadVideo(exercise.videoUrl, 0F)
                }
            })
        }

        holder.playButton.setOnClickListener {
            collapseCardView(holder.cardView, holder.itemView)
            val playerCallback = object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.pause()
                }
            }
            holder.youTubePlayerView.getYouTubePlayerWhenReady(playerCallback)
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val youTubePlayerView: YouTubePlayerView = itemView.findViewById(R.id.youtubePlayerView)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val playButton: Button = itemView.findViewById(R.id.playButton)
        val exerciseNameTextView: TextView = itemView.findViewById(R.id.exerciseNameTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)

    }

    private fun expandCardView(cardView: CardView) {
        originalHeights[cardView.hashCode()] = originalHeights[cardView.hashCode()] ?: cardView.height

        cardView.measure(View.MeasureSpec.makeMeasureSpec(cardView.width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        val targetHeight = cardView.measuredHeight

        val expandAnimation = ValueAnimator.ofInt(cardView.height, targetHeight)
        expandAnimation.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            cardView.layoutParams.height = animatedValue
            cardView.requestLayout()
        }
        expandAnimation.duration = 300
        expandAnimation.start()

        cardView.findViewById<View>(R.id.youtubePlayerView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.descriptionTextView).visibility = View.VISIBLE
        cardView.findViewById<View>(R.id.playButton).visibility = View.VISIBLE
    }

    private fun collapseCardView(cardView: CardView, itemView: View) {
        val originalHeight = originalHeights[cardView.hashCode()] ?: ViewGroup.LayoutParams.WRAP_CONTENT

        val collapseAnimation = AnimationUtils.loadAnimation(itemView.context, R.anim.collapse_anim)
        collapseAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                cardView.layoutParams.height = originalHeight
                cardView.requestLayout()
            }
        })

        cardView.startAnimation(collapseAnimation)

        originalHeights[cardView.hashCode()] = originalHeights[cardView.hashCode()] ?: cardView.height

        val initialHeight = cardView.height
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: android.view.animation.Transformation?) {
                val newHeight = (initialHeight * (1 - interpolatedTime)).toInt()
                cardView.layoutParams.height = max(newHeight, originalHeight)
                cardView.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        animation.duration = 500
        cardView.startAnimation(animation)
    }
}