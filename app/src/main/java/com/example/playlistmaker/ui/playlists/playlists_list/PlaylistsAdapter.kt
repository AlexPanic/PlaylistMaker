package com.example.playlistmaker.ui.playlists.playlists_list

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.playlists.model.Playlist

class PlaylistsAdapter(
    private val clickListener: PlaylistClickListener,
    @LayoutRes
    private val layout: Int,
) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {
    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder =
        PlaylistsViewHolder(parent, clickListener, layout)

    override fun getItemCount(): Int = playlists.size

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }

}