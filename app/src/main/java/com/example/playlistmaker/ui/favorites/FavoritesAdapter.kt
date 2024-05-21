package com.example.playlistmaker.ui.favorites

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.search.model.Track

class FavoritesAdapter(private val clickListener: TrackClickListener) :
    RecyclerView.Adapter<FavoritesViewHolder>() {
    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder =
        FavoritesViewHolder(parent, clickListener)


    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}