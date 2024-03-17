package com.example.playlistmaker.domain.mediateka.impl

import android.content.Context
import androidx.core.content.ContextCompat.getString
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.mediateka.FavoritesInteractor
import com.example.playlistmaker.domain.mediateka.model.Favorites
import java.util.concurrent.Executors

class FavoritesInteractorImpl(private val context: Context) : FavoritesInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun getFavorites(consumer: (Favorites?, String?) -> Unit) {
        executor.execute {
            consumer.invoke(null, getString(context, R.string.empty_favorites))
        }
    }
}