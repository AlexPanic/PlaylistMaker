package com.example.playlistmaker.ui.playlists.playlists_list

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlists.model.Playlist
import com.example.playlistmaker.ui.common.Helper


class PlaylistsViewHolder(
    parent: ViewGroup,
    private val clickListener: PlaylistsAdapter.PlaylistClickListener,
    @LayoutRes
    layout: Int
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layout, parent, false)
) {
    private val cover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)
    private val name: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tvTracksCount)
    fun bind(item: Playlist) {
        if (item.cover != null) {
            Glide.with(itemView)
                .load(Uri.parse(item.cover))
                .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                .into(cover)
        } else {
            cover.setImageResource(R.drawable.cover_placeholder)
        }
        name.text = item.name
        with(itemView.context) {
            tracksCount.text =
                buildString {
                    append(item.tracksCount)
                    append(" ")
                    append(
                        Helper.getWordFormDependingOnNumber(
                            item.tracksCount,
                            getString(R.string.wordFormForNumber1),
                            getString(R.string.wordFormForNumber2),
                            getString(R.string.wordFormForNumber5)
                        )
                    )
                }
        }
        itemView.setOnClickListener { clickListener.onPlaylistClick(playlist = item) }
    }
}
