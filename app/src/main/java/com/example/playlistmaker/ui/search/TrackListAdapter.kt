package com.example.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.search.model.Track

class TrackListAdapter(
    private val clickListener: TrackClickListener,
) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder(parent, clickListener)

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    interface TrackClickListener {
        fun onClick(track: Track)
        fun onLongClick(track: Track): Boolean
    }
}