package com.hamric.feature.genres.presentation.state

import com.hamric.core.model.Genre

sealed class GenresUiState {
    object Loading : GenresUiState()
    object Empty : GenresUiState()
    data class Success(val genres: List<Genre>) : GenresUiState()
    data class Error(val message: String) : GenresUiState()
}