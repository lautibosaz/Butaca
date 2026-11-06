package com.butaca.app.di

import com.butaca.app.data.repository.MovieRepository
import com.butaca.app.data.repository.MovieRepositoryImpl
import com.butaca.app.data.repository.WatchlistRepository
import com.butaca.app.data.repository.WatchlistRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMovieRepository(impl: MovieRepositoryImpl): MovieRepository

    @Binds
    @Singleton
    abstract fun bindWatchlistRepository(impl: WatchlistRepositoryImpl): WatchlistRepository
}
