package com.butaca.app.ui.detail

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.butaca.app.R
import com.butaca.app.core.TmdbImages
import com.butaca.app.core.UiState
import com.butaca.app.domain.model.CastMember
import com.butaca.app.domain.model.Movie
import com.butaca.app.domain.model.MovieDetail
import com.butaca.app.ui.components.ErrorState
import com.butaca.app.ui.components.FullScreenLoading
import com.butaca.app.ui.components.PosterCard
import com.butaca.app.ui.components.RatingBadge
import com.butaca.app.ui.components.RemoteImage
import com.butaca.app.ui.components.SectionHeader

@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isInWatchlist by viewModel.isInWatchlist.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedContent(
            targetState = state,
            contentKey = { it::class },
            transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(160)) },
            label = "detailContent"
        ) { current ->
            when (current) {
                UiState.Loading -> FullScreenLoading()
                is UiState.Error -> ErrorState(
                    error = current.error,
                    onRetry = viewModel::load,
                    modifier = Modifier.statusBarsPadding()
                )
                is UiState.Success -> DetailContent(
                    movie = current.data,
                    isInWatchlist = isInWatchlist,
                    onToggleWatchlist = viewModel::toggleWatchlist,
                    onMovieClick = onMovieClick
                )
            }
        }

        BackButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailContent(
    movie: MovieDetail,
    isInWatchlist: Boolean,
    onToggleWatchlist: () -> Unit,
    onMovieClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val scroll = rememberScrollState()
    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val backdropHeight = if (landscape) 240.dp else 330.dp
    val background = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(backdropHeight)
                    .graphicsLayer { translationY = scroll.value * 0.45f }
            ) {
                RemoteImage(
                    url = TmdbImages.backdrop(movie.backdropPath)
                        ?: TmdbImages.poster(movie.posterPath, "w780"),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.55f),
                                0.3f to Color.Transparent,
                                0.7f to background.copy(alpha = 0.65f),
                                1f to background
                            )
                        )
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = backdropHeight - 100.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                RemoteImage(
                    url = TmdbImages.poster(movie.posterPath),
                    contentDescription = stringResource(R.string.cd_poster, movie.title),
                    modifier = Modifier
                        .width(116.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    DetailMeta(movie)
                }
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            movie.tagline?.let {
                Text(
                    text = "“$it”",
                    style = MaterialTheme.typography.bodyLarge.copy(fontStyle = FontStyle.Italic),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (movie.genres.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    movie.genres.forEach { GenreTag(it) }
                }
            }

            WatchlistButton(saved = isInWatchlist, onClick = onToggleWatchlist)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryAction(
                    icon = Icons.Outlined.Share,
                    label = stringResource(R.string.action_share),
                    onClick = { ExternalActions.shareMovie(context, movie) }
                )
                movie.homepage?.let { url ->
                    SecondaryAction(
                        icon = Icons.Outlined.Public,
                        label = stringResource(R.string.action_homepage),
                        onClick = { ExternalActions.openHomepage(context, url) }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        SectionHeader(title = stringResource(R.string.detail_synopsis))
        Text(
            text = movie.overview.ifBlank { stringResource(R.string.detail_no_overview) },
            style = MaterialTheme.typography.bodyLarge,
            color = if (movie.overview.isBlank()) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onBackground
            },
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .widthIn(max = 720.dp)
        )

        FactsTable(movie)

        if (movie.cast.isNotEmpty()) {
            SectionHeader(
                title = stringResource(R.string.detail_cast),
                modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                itemsIndexed(movie.cast) { _, member -> CastItem(member) }
            }
        }

        MovieCarousel(
            title = stringResource(R.string.detail_recommendations),
            movies = movie.recommendations,
            onMovieClick = onMovieClick
        )
        MovieCarousel(
            title = stringResource(R.string.detail_similar),
            movies = movie.similar,
            onMovieClick = onMovieClick
        )

        Spacer(
            Modifier
                .navigationBarsPadding()
                .height(32.dp)
        )
    }
}

@Composable
private fun DetailMeta(movie: MovieDetail) {
    val parts = buildList {
        movie.releaseYear?.let { add(it) }
        movie.runtimeMinutes?.let { minutes ->
            val h = minutes / 60
            val m = minutes % 60
            add(
                if (h > 0) stringResource(R.string.detail_runtime, h, m)
                else stringResource(R.string.detail_runtime_minutes, m)
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (movie.rating > 0) {
            RatingBadge(
                rating = movie.rating,
                textStyle = MaterialTheme.typography.titleMedium,
                iconSize = 18.dp
            )
        }
        if (parts.isNotEmpty()) {
            Text(
                text = parts.joinToString("  ·  "),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GenreTag(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(2.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun WatchlistButton(saved: Boolean, onClick: () -> Unit) {
    val container by animateColorAsState(
        targetValue = if (saved) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary,
        label = "watchlistContainer"
    )
    val content by animateColorAsState(
        targetValue = if (saved) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary,
        label = "watchlistContent"
    )
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = container, contentColor = content),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        AnimatedContent(
            targetState = saved,
            transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.5f)) togetherWith fadeOut() },
            label = "watchlistIcon"
        ) { isSaved ->
            Icon(
                imageVector = if (isSaved) Icons.Rounded.Check else Icons.Rounded.Add,
                contentDescription = null
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(if (saved) R.string.action_remove_watchlist else R.string.action_add_watchlist_long),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun SecondaryAction(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FactsTable(movie: MovieDetail) {
    val facts = buildList {
        if (movie.originalTitle.isNotBlank() && movie.originalTitle != movie.title) {
            add(stringResource(R.string.detail_original_title) to movie.originalTitle)
        }
        movie.releaseYear?.let { add(stringResource(R.string.detail_release) to it) }
        if (movie.voteCount > 0) {
            add(stringResource(R.string.detail_votes) to "%,d".format(movie.voteCount))
        }
    }
    if (facts.isEmpty()) return
    Column(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        facts.forEach { (label, value) ->
            Row {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(120.dp)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun CastItem(member: CastMember) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(84.dp)
    ) {
        RemoteImage(
            url = TmdbImages.profile(member.profilePath),
            contentDescription = member.name,
            fallbackIcon = Icons.Outlined.Person,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (member.character.isNotBlank()) {
            Text(
                text = member.character,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Int) -> Unit
) {
    if (movies.isEmpty()) return
    SectionHeader(title = title, modifier = Modifier.padding(top = 28.dp, bottom = 12.dp))
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(movies) { _, movie ->
            PosterCard(movie = movie, onClick = { onMovieClick(movie.id) }, width = 116.dp)
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.action_back),
            tint = Color.White
        )
    }
}
