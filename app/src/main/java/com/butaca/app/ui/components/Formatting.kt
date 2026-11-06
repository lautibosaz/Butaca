package com.butaca.app.ui.components

import java.util.Locale

fun formatRating(rating: Double): String = String.format(Locale.getDefault(), "%.1f", rating)
