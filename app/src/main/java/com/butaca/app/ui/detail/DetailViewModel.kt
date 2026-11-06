package com.butaca.app.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.butaca.app.core.UiState
import com.butaca.app.core.loadAsUiState
import com.butaca.app.data.repository.MovieRepository
import com.butaca.app.data.repository.WatchlistRepository
import com.butaca.app.domain.model.MovieDetail
import com.butaca.app.ui.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle[Routes.ARG_MOVIE_ID])

    private val _uiState = MutableStateFlow<UiState<MovieDetail>>(UiState.Loading)
    val uiState: StateFlow<UiState<MovieDetail>> = _uiState.asStateFlow()

    val isInWatchlist: StateFlow<Boolean> = watchlistRepository.observeIsSaved(movieId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _uiState.value = loadAsUiState { movieRepository.movieDetail(movieId) }
        }
    }

    fun toggleWatchlist() {
        val detail = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { watchlistRepository.toggle(detail.toMovie()) }
    }
}
