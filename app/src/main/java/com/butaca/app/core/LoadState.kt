package com.butaca.app.core

import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> loadAsUiState(block: suspend () -> T): UiState<T> = try {
    UiState.Success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    UiState.Error(e.toAppError())
}
