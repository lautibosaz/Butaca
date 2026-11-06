package com.butaca.app.core

import java.util.Locale

fun tmdbLanguage(locale: Locale = Locale.getDefault()): String {
    val language = locale.language.ifBlank { "en" }
    val country = locale.country
    return when (language) {
        "es" -> if (country == "ES") "es-ES" else "es-MX"
        "en" -> if (country.isNotBlank()) "en-$country" else "en-US"
        "pt" -> if (country == "PT") "pt-PT" else "pt-BR"
        else -> if (country.isNotBlank()) "$language-$country" else language
    }
}
