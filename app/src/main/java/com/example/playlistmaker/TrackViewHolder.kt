package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.artworkUrl)
    private val track: TextView = itemView.findViewById(R.id.trackName)
    private val artist: TextView = itemView.findViewById(R.id.artistName)
    private val time: TextView =  itemView.findViewById(R.id.trackTime)
    fun bind(model: Track) {
        track.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTime()
        val cornerRadius = 2f// in design pixels
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .fitCenter()
            .transform(RoundedCorners(Helper.dpToPx(cornerRadius)))
            .into(cover)
    }
}