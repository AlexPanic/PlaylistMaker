package com.example.playlistmaker.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.common.Helper

class FavoritesViewHolder(
    parent: ViewGroup,
    private val clickListener: FavoritesAdapter.TrackClickListener,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(
        R.layout.list_item_track, parent, false
    )
) {
    private val cover: ImageView = itemView.findViewById(R.id.artworkUrl)
    private val track: TextView = itemView.findViewById(R.id.tvTrackName)
    private val artist: TextView = itemView.findViewById(R.id.tvArtistName)
    private val time: TextView = itemView.findViewById(R.id.tvTrackDuration)
    private val ivPlayTheTrackArrow: ImageView = itemView.findViewById(R.id.ivPlayTheTrackArrow)
    private val cornerRadius = 2f
    fun bind(model: Track) {
        track.text = model.trackName
        artist.text = model.artistName
        time.text = model.trackTime()
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.cover_placeholder)
            .transform(FitCenter(), RoundedCorners(Helper.dpToPx(this.cornerRadius)))
            .into(cover)


        if (model.previewUrl.isEmpty()) {
            ivPlayTheTrackArrow.isVisible = false
            itemView.setOnClickListener {}
        } else {
            ivPlayTheTrackArrow.isVisible = true
            itemView.setOnClickListener { clickListener.onTrackClick(track = model) }
        }
    }

}
