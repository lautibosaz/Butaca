package com.butaca.app.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.butaca.app.data.repository.MovieRepository
import com.butaca.app.data.repository.WatchlistRepository
import com.butaca.app.domain.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val watchlistRepository: WatchlistRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val query: StateFlow<String> = savedStateHandle.getStateFlow(KEY_QUERY, "")

    val activeQuery: StateFlow<String> = query
        .map { it.trim() }
        .debounce(SEARCH_DEBOUNCE_MS)
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.Eagerly, query.value.trim())

    val popularMovies: Flow<PagingData<Movie>> =
        movieRepository.popularMovies().cachedIn(viewModelScope)

    val searchResults: Flow<PagingData<Movie>> = activeQuery
        .flatMapLatest { q ->
            if (q.isEmpty()) flowOf(PagingData.empty<Movie>()) else movieRepository.searchMovies(q)
        }
        .cachedIn(viewModelScope)

    val watchlistIds: StateFlow<Set<Int>> = watchlistRepository.observeSavedIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    fun onQueryChange(value: String) {
        savedStateHandle[KEY_QUERY] = value
    }

    fun clearQuery() = onQueryChange("")

    fun toggleWatchlist(movie: Movie) {
        viewModelScope.launch { watchlistRepository.toggle(movie) }
    }

    private companion object {
        const val KEY_QUERY = "query"
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}
