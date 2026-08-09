package com.hamric.feature.details.presentation.state

import com.hamric.core.model.Movie
import com.hamric.core.model.Video

sealed class MovieDetailUiState {
    object Loading : MovieDetailUiState()
    data class Success(
        val movie: Movie,
        val trailer: Video? = null
    ) : MovieDetailUiState()
    data class SuccessWithError(
        val movie: Movie,
        val trailer: Video? = null,
        val message: String
    ) : MovieDetailUiState()
    data class Error(val message: String) : MovieDetailUiState()
}