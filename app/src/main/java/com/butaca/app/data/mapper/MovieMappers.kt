package com.butaca.app.data.mapper

import com.butaca.app.data.local.WatchlistEntity
import com.butaca.app.data.remote.dto.CastDto
import com.butaca.app.data.remote.dto.MovieDetailDto
import com.butaca.app.data.remote.dto.MovieDto
import com.butaca.app.domain.model.CastMember
import com.butaca.app.domain.model.Movie
import com.butaca.app.domain.model.MovieDetail

private fun String?.toYear(): String? = this?.take(4)?.takeIf { it.length == 4 }

fun MovieDto.toDomain(): Movie? {
    val movieId = id ?: return null
    return Movie(
        id = movieId,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = posterPath,
        backdropPath = backdropPath,
        rating = voteAverage ?: 0.0,
        releaseYear = releaseDate.toYear()
    )
}

fun CastDto.toDomain(): CastMember? {
    val castId = id ?: return null
    return CastMember(
        id = castId,
        name = name.orEmpty(),
        character = character.orEmpty(),
        profilePath = profilePath
    )
}

fun MovieDetailDto.toDomain(): MovieDetail = MovieDetail(
    id = id ?: 0,
    title = title.orEmpty(),
    originalTitle = originalTitle.orEmpty(),
    tagline = tagline?.takeIf { it.isNotBlank() },
    overview = overview.orEmpty(),
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    releaseYear = releaseDate.toYear(),
    runtimeMinutes = runtime?.takeIf { it > 0 },
    genres = genres.orEmpty().mapNotNull { it.name?.takeIf(String::isNotBlank) },
    homepage = homepage?.takeIf { it.startsWith("http") },
    cast = credits?.cast.orEmpty().mapNotNull { it.toDomain() }.take(MAX_CAST),
    similar = similar?.results.orEmpty().mapNotNull { it.toDomain() },
    recommendations = recommendations?.results.orEmpty().mapNotNull { it.toDomain() }
)

fun WatchlistEntity.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = rating,
    releaseYear = releaseYear
)

fun Movie.toWatchlistEntity(addedAt: Long = System.currentTimeMillis()) = WatchlistEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    rating = rating,
    releaseYear = releaseYear,
    addedAt = addedAt
)

private const val MAX_CAST = 15
