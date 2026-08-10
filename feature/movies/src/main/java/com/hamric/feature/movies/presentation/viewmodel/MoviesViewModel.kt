package com.hamric.feature.movies.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hamric.core.model.Movie
import com.hamric.feature.movies.domain.usecase.GetMoviesByGenreUseCase
import com.hamric.feature.movies.presentation.ui.state.MoviesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesByGenreUseCase: GetMoviesByGenreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MoviesUiState>(MoviesUiState.Idle)
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    private val _movies = MutableStateFlow<Flow<PagingData<Movie>>?>(null)
    val movies: StateFlow<Flow<PagingData<Movie>>?> = _movies.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val genreChangeTrigger = MutableSharedFlow<Int>(extraBufferCapacity = 1)

    private val _genreId = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            genreChangeTrigger
                .distinctUntilChanged()
                .collect { genreId ->
                    fetchMovies(genreId)
                }
        }

        viewModelScope.launch {
            refreshTrigger
                .collect {
                    _genreId.value?.let { genreId ->
                        fetchMovies(genreId)
                    }
                }
        }
    }

    private fun fetchMovies(genreId: Int) {
        if (genreId <= 0) {
            _uiState.update { MoviesUiState.Error("Invalid genre ID") }
            return
        }

        _uiState.update { MoviesUiState.Loading }
        _genreId.value = genreId

        try {
            val pagingFlow = getMoviesByGenreUseCase(genreId)
                .cachedIn(viewModelScope)
            _movies.value = pagingFlow
            _uiState.update { MoviesUiState.Success }
        } catch (e: Exception) {
            Log.e("MoviesViewModel", "Error loading movies for genre $genreId", e)

            val userMessage = when (e) {
                is java.net.SocketTimeoutException -> "Connection timeout. Please try again."
                is java.io.IOException -> "Network error. Please check your connection."
                else -> "Unable to load movies. Please try again."
            }
            _uiState.update { MoviesUiState.Error(userMessage) }
        }
    }

    fun loadMovies(genreId: Int) {
        if (genreId <= 0) {
            _uiState.update { MoviesUiState.Error("Invalid genre ID") }
            return
        }

        viewModelScope.launch {
            genreChangeTrigger.emit(genreId)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _movies.value = null
            refreshTrigger.emit(Unit)
        }
    }

    fun retry() {
        val currentState = _uiState.value
        when {
            currentState is MoviesUiState.Error -> {
                _genreId.value?.let { loadMovies(it) }
                    ?: run { refresh() }
            }
            else -> refresh()
        }
    }

    fun clearError() {
        if (_uiState.value is MoviesUiState.Error) {
            _uiState.update { MoviesUiState.Idle }
        }
    }

    public override fun onCleared() {
        super.onCleared()
        _movies.value = null
    }
}

