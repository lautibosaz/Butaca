package com.butaca.app.ui.detail

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.butaca.app.R
import com.butaca.app.domain.model.MovieDetail

object ExternalActions {

    fun shareMovie(context: Context, movie: MovieDetail) {
        val title = movie.releaseYear?.let { "${movie.title} ($it)" } ?: movie.title
        val text = buildString {
            append(title)
            if (movie.overview.isNotBlank()) {
                append("\n\n")
                append(movie.overview)
            }
            append("\n\n")
            append("https://www.themoviedb.org/movie/${movie.id}")
        }
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startSafely(context, Intent.createChooser(send, context.getString(R.string.share_chooser)))
    }

    fun openHomepage(context: Context, url: String) {
        startSafely(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun startSafely(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_app_to_open, Toast.LENGTH_SHORT).show()
        }
    }
}
