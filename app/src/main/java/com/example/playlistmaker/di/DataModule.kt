package com.example.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.ItunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ItunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApiService::class.java)
    }
    single {
        androidContext()
            .getSharedPreferences("local_storage", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}