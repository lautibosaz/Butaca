package com.butaca.app.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.butaca.app.R

@Composable
fun ButacaWordmark(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.app_name).uppercase(),
        style = MaterialTheme.typography.headlineSmall.copy(
            fontSize = 26.sp,
            letterSpacing = 0.14.em
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}
