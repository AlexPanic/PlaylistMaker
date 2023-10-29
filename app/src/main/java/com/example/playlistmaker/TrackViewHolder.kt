package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView
    private val track: TextView
    private val artist: TextView
    private val time: TextView
    init {
        cover = itemView.findViewById(R.id.artworkUrl)
        track = itemView.findViewById(R.id.trackName)
        artist = itemView.findViewById(R.id.artistName)
        time =  itemView.findViewById(R.id.trackTime)
    }
    fun bind(model: Track) {
        track.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(4))
            .into(cover)
    }
}