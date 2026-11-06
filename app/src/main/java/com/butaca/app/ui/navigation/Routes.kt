package com.butaca.app.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector
import com.butaca.app.R

object Routes {
    const val HOME = "home"
    const val TRENDING = "trending"
    const val WATCHLIST = "watchlist"
    const val ARG_MOVIE_ID = "movieId"
    const val DETAIL = "detail/{$ARG_MOVIE_ID}"

    fun detail(movieId: Int) = "detail/$movieId"
}

enum class TopLevelTab(
    val route: String,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME(Routes.HOME, R.string.tab_home, Icons.Filled.Home, Icons.Outlined.Home),
    TRENDING(Routes.TRENDING, R.string.tab_trending, Icons.Filled.Whatshot, Icons.Outlined.Whatshot),
    WATCHLIST(Routes.WATCHLIST, R.string.tab_watchlist, Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
}
