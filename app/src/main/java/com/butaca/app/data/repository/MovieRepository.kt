package com.butaca.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.butaca.app.data.mapper.toDomain
import com.butaca.app.data.remote.MoviePagingSource
import com.butaca.app.data.remote.TmdbApi
import com.butaca.app.domain.model.Movie
import com.butaca.app.domain.model.MovieDetail
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

interface MovieRepository {
    fun popularMovies(): Flow<PagingData<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    suspend fun trendingThisWeek(): List<Movie>
    suspend fun movieDetail(movieId: Int): MovieDetail
}

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val api: TmdbApi,
    @Named(IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher
) : MovieRepository {

    private val pagingConfig = PagingConfig(
        pageSize = TMDB_PAGE_SIZE,
        prefetchDistance = 6,
        enablePlaceholders = false
    )

    override fun popularMovies(): Flow<PagingData<Movie>> =
        Pager(pagingConfig) { MoviePagingSource { page -> api.getPopularMovies(page) } }.flow

    override fun searchMovies(query: String): Flow<PagingData<Movie>> =
        Pager(pagingConfig) { MoviePagingSource { page -> api.searchMovies(query, page) } }.flow

    override suspend fun trendingThisWeek(): List<Movie> = withContext(ioDispatcher) {
        api.getTrendingMoviesOfWeek().results.orEmpty().mapNotNull { it.toDomain() }
    }

    override suspend fun movieDetail(movieId: Int): MovieDetail = withContext(ioDispatcher) {
        api.getMovieDetail(movieId).toDomain()
    }

    private companion object {
        const val TMDB_PAGE_SIZE = 20
    }
}

const val IO_DISPATCHER = "io_dispatcher"
