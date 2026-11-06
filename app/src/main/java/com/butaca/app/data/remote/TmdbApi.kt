package com.butaca.app.data.remote

import com.butaca.app.data.remote.dto.MovieDetailDto
import com.butaca.app.data.remote.dto.MoviePageDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int): MoviePageDto

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("include_adult") includeAdult: Boolean = false
    ): MoviePageDto

    @GET("trending/movie/week")
    suspend fun getTrendingMoviesOfWeek(@Query("page") page: Int = 1): MoviePageDto

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int,
        @Query("append_to_response") append: String = "credits,similar,recommendations"
    ): MovieDetailDto
}
