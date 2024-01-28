package com.example.playlistmaker.presentation.ui.search

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Helper
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.artworkUrl)
    private val track: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artist: TextView = itemView.findViewById(R.id.tvArtistName)
    private val time: TextView =  itemView.findViewById(R.id.tvTrackDuration)
    fun bind(model: Track) {
        track.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTime()
        val cornersRadius = 2f
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(Helper.dpToPx(cornersRadius)))
            .into(cover)
    }
}