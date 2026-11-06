package com.butaca.app.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.butaca.app.data.mapper.toDomain
import com.butaca.app.data.remote.dto.MoviePageDto
import com.butaca.app.domain.model.Movie
import kotlin.coroutines.cancellation.CancellationException

class MoviePagingSource(
    private val fetchPage: suspend (page: Int) -> MoviePageDto
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val response = fetchPage(page)
            val movies = response.results.orEmpty().mapNotNull { it.toDomain() }
            val totalPages = minOf(response.totalPages ?: page, MAX_PAGE)
            LoadResult.Page(
                data = movies,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (page < totalPages) page + 1 else null
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? =
        state.anchorPosition?.let { anchor ->
            val page = state.closestPageToPosition(anchor)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }

    private companion object {
        const val FIRST_PAGE = 1
        const val MAX_PAGE = 500
    }
}
