package com.hamric.feature.details.presentation.state

sealed class ReviewsState {
    object Loading : ReviewsState()
    object Success : ReviewsState()
    data class Error(val message: String) : ReviewsState()
}