package com.butaca.app.di

import android.content.Context
import androidx.room.Room
import com.butaca.app.data.local.ButacaDatabase
import com.butaca.app.data.local.WatchlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ButacaDatabase =
        Room.databaseBuilder(context, ButacaDatabase::class.java, "butaca.db").build()

    @Provides
    fun provideWatchlistDao(database: ButacaDatabase): WatchlistDao = database.watchlistDao()
}
