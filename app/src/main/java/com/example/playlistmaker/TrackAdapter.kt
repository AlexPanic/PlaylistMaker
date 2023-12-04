package com.example.playlistmaker

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val tracks: MutableList<Track>, private val addToHistoryByClick: Boolean = true) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun getItemCount(): Int {
        return tracks.count()
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        if (addToHistoryByClick) {
            holder.itemView.setOnClickListener{
                val trackSearchHistory = TrackSearchHistory(App.getSharedPreferences())
                trackSearchHistory.addTrack(tracks[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }
}