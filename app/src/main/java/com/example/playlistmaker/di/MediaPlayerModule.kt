package com.example.playlistmaker.di

import android.media.AudioAttributes
import android.media.MediaPlayer
import org.koin.dsl.module

val mediaPlayerModule = module {
    single {
        MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }
}