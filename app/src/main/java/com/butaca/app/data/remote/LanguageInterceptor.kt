package com.butaca.app.data.remote

import com.butaca.app.core.tmdbLanguage
import okhttp3.Interceptor
import okhttp3.Response

class LanguageInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url.newBuilder()
            .setQueryParameter("language", tmdbLanguage())
            .build()
        return chain.proceed(original.newBuilder().url(url).build())
    }
}
