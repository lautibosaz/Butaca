package com.butaca.app.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.butaca.app.R
import com.butaca.app.core.TmdbImages
import com.butaca.app.domain.model.Movie
import com.butaca.app.ui.components.RatingBadge
import com.butaca.app.ui.components.RemoteImage
import com.butaca.app.ui.components.pressScaleClickable

@Composable
fun HeroBanner(
    movie: Movie,
    isInWatchlist: Boolean,
    landscape: Boolean,
    onOpen: () -> Unit,
    onToggleWatchlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(4.dp)
    val sizeModifier = if (landscape) {
        Modifier.height(300.dp)
    } else {
        Modifier.aspectRatio(0.72f)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .then(sizeModifier)
            .clip(shape)
            .pressScaleClickable(pressedScale = 0.985f, onClick = onOpen)
    ) {
        RemoteImage(
            url = if (landscape) {
                TmdbImages.backdrop(movie.backdropPath) ?: TmdbImages.poster(movie.posterPath, "w780")
            } else {
                TmdbImages.poster(movie.posterPath, "w780")
            },
            contentDescription = stringResource(R.string.cd_poster, movie.title),
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    if (landscape) {
                        Brush.horizontalGradient(
                            0f to Color.Black.copy(alpha = 0.88f),
                            0.6f to Color.Black.copy(alpha = 0.35f),
                            1f to Color.Transparent
                        )
                    } else {
                        Brush.verticalGradient(
                            0.35f to Color.Transparent,
                            0.7f to Color.Black.copy(alpha = 0.55f),
                            1f to Color.Black.copy(alpha = 0.92f)
                        )
                    }
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .then(if (landscape) Modifier.fillMaxWidth(0.55f) else Modifier.fillMaxWidth())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.hero_label).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                movie.releaseYear?.let {
                    Text(it, style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f))
                }
                if (movie.rating > 0) RatingBadge(rating = movie.rating, textColor = Color.White)
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onOpen,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.action_details))
                }
                OutlinedButton(
                    onClick = onToggleWatchlist,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        contentColor = Color.White
                    )
                ) {
                    AnimatedContent(
                        targetState = isInWatchlist,
                        transitionSpec = { (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith fadeOut() },
                        label = "heroWatchlistIcon"
                    ) { saved ->
                        Icon(
                            imageVector = if (saved) Icons.Rounded.Check else Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(if (isInWatchlist) R.string.action_in_watchlist else R.string.action_add_watchlist)
                    )
                }
            }
        }
    }
}
