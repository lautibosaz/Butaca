package com.butaca.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.butaca.app.R
import com.butaca.app.core.TmdbImages
import com.butaca.app.domain.model.Movie
import com.butaca.app.ui.theme.CondensedFamily

private val PosterShape = RoundedCornerShape(3.dp)

@Composable
fun MovieRow(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    rank: Int? = null
) {
    Column(modifier = modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RemoteImage(
                url = TmdbImages.poster(movie.posterPath, "w342"),
                contentDescription = stringResource(R.string.cd_poster, movie.title),
                modifier = Modifier
                    .width(92.dp)
                    .aspectRatio(2f / 3f)
                    .clip(PosterShape)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (rank != null) {
                    Text(
                        text = "%02d".format(rank),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                MovieMeta(movie)
                if (movie.overview.isNotBlank()) {
                    Text(
                        text = movie.overview,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 128.dp, end = 20.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun MovieMeta(movie: Movie, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        movie.releaseYear?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (movie.rating > 0) RatingBadge(rating = movie.rating)
    }
}

@Composable
fun PosterCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp? = null
) {
    val sized = if (width != null) modifier.width(width) else modifier
    Column(modifier = sized.pressScaleClickable(onClick = onClick)) {
        RemoteImage(
            url = TmdbImages.poster(movie.posterPath, "w342"),
            contentDescription = stringResource(R.string.cd_poster, movie.title),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(PosterShape)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = movie.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        movie.releaseYear?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RankedMovieRow(
    movie: Movie,
    rank: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pressScaleClickable(pressedScale = 0.98f, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.width(if (rank >= 10) 84.dp else 56.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = CondensedFamily,
                    fontSize = 88.sp,
                    lineHeight = 88.sp,
                    drawStyle = Stroke(width = 4f)
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false
            )
        }
        RemoteImage(
            url = TmdbImages.poster(movie.posterPath, "w342"),
            contentDescription = stringResource(R.string.cd_poster, movie.title),
            modifier = Modifier
                .width(84.dp)
                .aspectRatio(2f / 3f)
                .clip(PosterShape)
        )
        Spacer(Modifier.size(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            MovieMeta(movie)
            if (movie.overview.isNotBlank()) {
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MovieRowSkeleton(alpha: Float, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SkeletonBlock(
            Modifier
                .width(92.dp)
                .aspectRatio(2f / 3f),
            alpha
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SkeletonBlock(Modifier.width(28.dp).height(12.dp), alpha)
            SkeletonBlock(Modifier.fillMaxWidth(0.8f).height(18.dp), alpha)
            SkeletonBlock(Modifier.width(90.dp).height(14.dp), alpha)
            SkeletonBlock(Modifier.fillMaxWidth().height(12.dp), alpha)
            SkeletonBlock(Modifier.fillMaxWidth(0.6f).height(12.dp), alpha)
        }
    }
}
