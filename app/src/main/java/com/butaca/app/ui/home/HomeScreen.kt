package com.butaca.app.ui.home

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.butaca.app.R
import com.butaca.app.core.toAppError
import com.butaca.app.domain.model.Movie
import com.butaca.app.ui.components.ButacaWordmark
import com.butaca.app.ui.components.ErrorState
import com.butaca.app.ui.components.MessageState
import com.butaca.app.ui.components.MovieRow
import com.butaca.app.ui.components.MovieRowSkeleton
import com.butaca.app.ui.components.SectionHeader
import com.butaca.app.ui.components.SkeletonBlock
import com.butaca.app.ui.components.pagingFooter
import com.butaca.app.ui.components.rememberPulseAlpha

@Composable
fun rememberListColumns(): Int {
    val configuration = LocalConfiguration.current
    val wide = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
        configuration.screenWidthDp >= 600
    return if (wide) 2 else 1
}

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val activeQuery by viewModel.activeQuery.collectAsStateWithLifecycle()
    val watchlistIds by viewModel.watchlistIds.collectAsStateWithLifecycle()
    val popular = viewModel.popularMovies.collectAsLazyPagingItems()
    val results = viewModel.searchResults.collectAsLazyPagingItems()

    val popularGridState = rememberLazyGridState()
    val searchGridState = rememberLazyGridState()
    val columns = rememberListColumns()
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 12.dp)
        ) {
            ButacaWordmark()
            Spacer(Modifier.height(12.dp))
            SearchField(
                query = query,
                onQueryChange = viewModel::onQueryChange,
                onClear = viewModel::clearQuery
            )
        }

        AnimatedContent(
            targetState = query.isNotBlank(),
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
            label = "homeContent"
        ) { searching ->
            if (searching) {
                SearchResults(
                    query = query.trim(),
                    isWaitingForInput = query.trim() != activeQuery,
                    results = results,
                    columns = columns,
                    gridState = searchGridState,
                    onMovieClick = onMovieClick
                )
            } else {
                PopularFeed(
                    popular = popular,
                    watchlistIds = watchlistIds,
                    columns = columns,
                    landscape = landscape,
                    gridState = popularGridState,
                    onMovieClick = onMovieClick,
                    onToggleWatchlist = viewModel::toggleWatchlist
                )
            }
        }
    }
}

@Composable
private fun PopularFeed(
    popular: LazyPagingItems<Movie>,
    watchlistIds: Set<Int>,
    columns: Int,
    landscape: Boolean,
    gridState: LazyGridState,
    onMovieClick: (Int) -> Unit,
    onToggleWatchlist: (Movie) -> Unit
) {
    val refresh = popular.loadState.refresh
    when {
        refresh is LoadState.Loading && popular.itemCount == 0 -> FeedSkeleton(columns)
        refresh is LoadState.Error && popular.itemCount == 0 ->
            ErrorState(error = refresh.error.toAppError(), onRetry = popular::retry)
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (popular.itemCount > 0) {
                item(key = "hero", span = { GridItemSpan(maxLineSpan) }, contentType = "hero") {
                    popular[0]?.let { hero ->
                        HeroBanner(
                            movie = hero,
                            isInWatchlist = hero.id in watchlistIds,
                            landscape = landscape,
                            onOpen = { onMovieClick(hero.id) },
                            onToggleWatchlist = { onToggleWatchlist(hero) }
                        )
                    }
                }
            }
            item(key = "popularHeader", span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
                SectionHeader(
                    title = stringResource(R.string.section_popular),
                    subtitle = stringResource(R.string.section_popular_subtitle),
                    modifier = Modifier.padding(top = 28.dp, bottom = 4.dp)
                )
            }
            items(count = (popular.itemCount - 1).coerceAtLeast(0), contentType = { "movie" }) { index ->
                popular[index + 1]?.let { movie ->
                    MovieRow(movie = movie, rank = index + 2, onClick = { onMovieClick(movie.id) })
                }
            }
            pagingFooter(popular)
        }
    }
}

@Composable
private fun SearchResults(
    query: String,
    isWaitingForInput: Boolean,
    results: LazyPagingItems<Movie>,
    columns: Int,
    gridState: LazyGridState,
    onMovieClick: (Int) -> Unit
) {
    val refresh = results.loadState.refresh
    when {
        isWaitingForInput || refresh is LoadState.Loading -> ListSkeleton(columns)
        refresh is LoadState.Error ->
            ErrorState(error = refresh.error.toAppError(), onRetry = results::retry)
        results.itemCount == 0 -> MessageState(
            icon = Icons.Outlined.SearchOff,
            title = stringResource(R.string.search_no_results_title),
            message = stringResource(R.string.search_no_results_message, query)
        )
        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            state = gridState,
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = "resultsHeader", span = { GridItemSpan(maxLineSpan) }, contentType = "header") {
                SectionHeader(
                    title = stringResource(R.string.section_results, query),
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(count = results.itemCount, contentType = { "movie" }) { index ->
                results[index]?.let { movie ->
                    MovieRow(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
            pagingFooter(results)
        }
    }
}

@Composable
private fun FeedSkeleton(columns: Int) {
    val alpha = rememberPulseAlpha()
    val landscape = columns > 1
    Column(Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        SkeletonBlock(
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(if (landscape) 220.dp else 360.dp),
            alpha
        )
        repeat(3) { MovieRowSkeleton(alpha) }
    }
}

@Composable
private fun ListSkeleton(columns: Int) {
    val alpha = rememberPulseAlpha()
    Column(Modifier.fillMaxSize()) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().height(2.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        repeat(if (columns > 1) 2 else 5) { MovieRowSkeleton(alpha) }
    }
}
