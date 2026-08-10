package com.hamric.feature.movies.presentation.ui.state

sealed class MoviesUiState {
    object Idle : MoviesUiState()
    object Loading : MoviesUiState()
    object Success : MoviesUiState()
    data class Error(val message: String) : MoviesUiState()
}