package com.butaca.app.ui.components

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.butaca.app.core.toAppError

fun LazyGridScope.pagingFooter(items: LazyPagingItems<*>) {
    when (val append = items.loadState.append) {
        is LoadState.Loading -> item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
            FooterLoading()
        }
        is LoadState.Error -> item(span = { GridItemSpan(maxLineSpan) }, contentType = "footer") {
            InlineError(error = append.error.toAppError(), onRetry = items::retry)
        }
        is LoadState.NotLoading -> Unit
    }
}
