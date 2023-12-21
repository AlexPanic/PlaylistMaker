package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val listener: ItemClickListener,
    private val tracks: MutableList<Track>,
    private val history: TrackSearchHistory? = null
) : RecyclerView.Adapter<TrackViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(position: Int, fromHistory: Boolean)
    }

    override fun getItemCount(): Int {
        return tracks.count()
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            history?.addTrack(tracks[position])
            listener.onItemClick(position, history == null)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }
}