package com.example.playlistmaker.presentation.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

class TrackListAdapter(
    private val listener: ItemClickListener,
    private val isHistoryAdapter: Boolean
) : RecyclerView.Adapter<TrackViewHolder>() {

    lateinit var tracks: MutableList<Track>

    interface ItemClickListener {
        fun onItemClick(track: Track, isHistoryAdapter: Boolean)
    }

    override fun getItemCount(): Int {
        return tracks.count()
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            listener.onItemClick(tracks[position], isHistoryAdapter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }
}