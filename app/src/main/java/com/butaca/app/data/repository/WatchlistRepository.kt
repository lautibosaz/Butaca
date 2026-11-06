package com.butaca.app.data.repository

import com.butaca.app.data.local.WatchlistDao
import com.butaca.app.data.mapper.toDomain
import com.butaca.app.data.mapper.toWatchlistEntity
import com.butaca.app.domain.model.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface WatchlistRepository {
    fun observeWatchlist(): Flow<List<Movie>>
    fun observeSavedIds(): Flow<Set<Int>>
    fun observeIsSaved(movieId: Int): Flow<Boolean>
    suspend fun toggle(movie: Movie)
}

@Singleton
class WatchlistRepositoryImpl @Inject constructor(
    private val dao: WatchlistDao
) : WatchlistRepository {

    override fun observeWatchlist(): Flow<List<Movie>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeSavedIds(): Flow<Set<Int>> = dao.observeIds().map { it.toSet() }

    override fun observeIsSaved(movieId: Int): Flow<Boolean> = dao.observeIsSaved(movieId)

    override suspend fun toggle(movie: Movie) {
        if (dao.isSaved(movie.id)) dao.delete(movie.id) else dao.insert(movie.toWatchlistEntity())
    }
}
