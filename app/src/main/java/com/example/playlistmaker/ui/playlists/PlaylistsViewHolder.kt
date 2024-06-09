package com.example.playlistmaker.ui.playlists

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.list_item_playlist, parent, false)
) {
    private val cover: ImageView = itemView.findViewById(R.id.ivPlaylistCover)
    private val name: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val tracksCount: TextView = itemView.findViewById(R.id.tvTracksCount)
    fun bind(model: Playlist) {
        if (model.cover != null) {
            Glide.with(itemView)
                .load(Uri.parse(model.cover))
                .transform(CenterCrop(), RoundedCorners(Helper.dpToPx(Helper.COVER_RADIUS)))
                .into(cover)
        }
        name.text = model.name
        with(itemView.context) {
            tracksCount.text =
                model.tracksCount.toString() + " " + Helper.getWordFormDependingOnNumber(
                    model.tracksCount,
                    getString(R.string.wordFormForNumber1),
                    getString(R.string.wordFormForNumber2),
                    getString(R.string.wordFormForNumber5)
                )
        }
    }
}
