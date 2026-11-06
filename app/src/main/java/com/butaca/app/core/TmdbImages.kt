package com.butaca.app.core

import com.butaca.app.BuildConfig

object TmdbImages {
    private val BASE = BuildConfig.TMDB_IMAGE_BASE_URL

    fun poster(path: String?, size: String = "w500"): String? = build(path, size)
    fun backdrop(path: String?, size: String = "w1280"): String? = build(path, size)
    fun profile(path: String?, size: String = "w185"): String? = build(path, size)

    private fun build(path: String?, size: String): String? =
        if (path.isNullOrBlank()) null else "$BASE$size$path"
}
