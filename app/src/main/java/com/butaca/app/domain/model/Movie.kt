package com.butaca.app.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val releaseYear: String?
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val originalTitle: String,
    val tagline: String?,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val rating: Double,
    val voteCount: Int,
    val releaseYear: String?,
    val runtimeMinutes: Int?,
    val genres: List<String>,
    val homepage: String?,
    val cast: List<CastMember>,
    val similar: List<Movie>,
    val recommendations: List<Movie>
) {
    fun toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = rating,
        releaseYear = releaseYear
    )
}

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
)
