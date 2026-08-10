package com.hamric.feature.details.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hamric.core.model.Review
import com.hamric.feature.details.domain.usecase.GetMovieDetailsUseCase
import com.hamric.feature.details.domain.usecase.GetMovieReviewsUseCase
import com.hamric.feature.details.domain.usecase.GetMovieTrailerUseCase
import com.hamric.feature.details.presentation.state.MovieDetailUiState
import com.hamric.feature.details.presentation.state.ReviewsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val getMovieReviewsUseCase: GetMovieReviewsUseCase,
    private val getMovieTrailerUseCase: GetMovieTrailerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailUiState>(MovieDetailUiState.Loading)
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    private val _reviewsState = MutableStateFlow<ReviewsState>(ReviewsState.Loading)
    val reviewsState: StateFlow<ReviewsState> = _reviewsState.asStateFlow()

    private val _reviews = MutableStateFlow<Flow<PagingData<Review>>?>(null)
    val reviews: StateFlow<Flow<PagingData<Review>>?> = _reviews.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val movieIdTrigger = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    private val reviewsTrigger = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    private val retryReviewsTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _movieId = MutableStateFlow<Int?>(null)

    init {
        viewModelScope.launch {
            movieIdTrigger.collect { movieId ->
                fetchMovieDetails(movieId)
            }
        }

        viewModelScope.launch {
            refreshTrigger.collect {
                val movieId = _movieId.value
                if (movieId != null) {
                    _reviews.value = null
                    _reviewsState.update { ReviewsState.Loading }
                    fetchMovieDetails(movieId)
                    fetchAndUpdateReviews(movieId)
                }
            }
        }

        viewModelScope.launch {
            reviewsTrigger.collect { movieId ->
                fetchAndUpdateReviews(movieId)
            }
        }

        viewModelScope.launch {
            retryReviewsTrigger.collect {
                val movieId = _movieId.value
                if (movieId != null) {
                    _reviews.value = null
                    fetchAndUpdateReviews(movieId)
                }
            }
        }
    }

    private fun fetchMovieDetails(movieId: Int) {
        if (movieId <= 0) {
            _uiState.update { MovieDetailUiState.Error("Invalid movie ID") }
            return
        }

        if (!_isRefreshing.value) {
            _uiState.update { MovieDetailUiState.Loading }
        }

        _movieId.value = movieId

        viewModelScope.launch {
            try {
                val detailsDeferred = async { getMovieDetailsUseCase(movieId) }
                val trailerDeferred = async { getMovieTrailerUseCase(movieId) }

                val detailsResult = detailsDeferred.await()
                val trailerResult = trailerDeferred.await()

                detailsResult.fold(
                    onSuccess = { movie ->
                        if (movie.id <= 0 || movie.title.isBlank()) {
                            _uiState.update {
                                MovieDetailUiState.Error("Invalid movie data received")
                            }
                            return@fold
                        }

                        val trailer = trailerResult.getOrNull()
                        _uiState.update { MovieDetailUiState.Success(movie, trailer) }

                        if (_isRefreshing.value) {
                            _isRefreshing.update { false }
                        }
                    },
                    onFailure = { exception ->
                        Log.e("MovieDetailViewModel", "Error loading movie $movieId", exception)

                        val message = when (exception) {
                            is java.net.SocketTimeoutException -> "Connection timeout. Please try again."
                            is java.io.IOException -> "Network error. Please check your connection."
                            else -> "Unable to load movie details. Please try again."
                        }

                        val currentState = _uiState.value
                        if (currentState is MovieDetailUiState.Success) {
                            _uiState.update {
                                MovieDetailUiState.SuccessWithError(
                                    movie = currentState.movie,
                                    trailer = currentState.trailer,
                                    message = message
                                )
                            }
                        } else {
                            _uiState.update { MovieDetailUiState.Error(message) }
                        }

                        if (_isRefreshing.value) {
                            _isRefreshing.update { false }
                        }
                    }
                )
            } catch (e: Exception) {
                Log.e("MovieDetailViewModel", "Error loading movie $movieId", e)

                val message = when (e) {
                    is java.net.SocketTimeoutException -> "Connection timeout. Please try again."
                    is java.io.IOException -> "Network error. Please check your connection."
                    else -> "Unable to load movie details. Please try again."
                }

                val currentState = _uiState.value
                if (currentState is MovieDetailUiState.Success) {
                    _uiState.update {
                        MovieDetailUiState.SuccessWithError(
                            movie = currentState.movie,
                            trailer = currentState.trailer,
                            message = message
                        )
                    }
                } else {
                    _uiState.update { MovieDetailUiState.Error(message) }
                }

                if (_isRefreshing.value) {
                    _isRefreshing.update { false }
                }
            }
        }
    }

    private fun fetchAndUpdateReviews(movieId: Int) {
        if (movieId <= 0) {
            _reviewsState.update { ReviewsState.Error("Invalid movie ID") }
            return
        }

        if (!_isRefreshing.value || _reviews.value == null) {
            _reviewsState.update { ReviewsState.Loading }
        }

        viewModelScope.launch {
            try {
                val pagingFlow = getMovieReviewsUseCase(movieId)
                    .cachedIn(viewModelScope)
                _reviews.value = pagingFlow
                _reviewsState.update { ReviewsState.Success }

                if (_isRefreshing.value) {
                    _isRefreshing.update { false }
                }
            } catch (e: Exception) {
                Log.e("MovieDetailViewModel", "Error loading reviews for movie $movieId", e)
                val message = when (e) {
                    is java.net.SocketTimeoutException -> "Connection timeout. Please try again."
                    is java.io.IOException -> "Network error. Please check your connection."
                    else -> "Unable to load reviews. Please try again."
                }
                _reviewsState.update { ReviewsState.Error(message) }

                if (_isRefreshing.value) {
                    _isRefreshing.update { false }
                }
            }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        if (movieId <= 0) {
            _uiState.update { MovieDetailUiState.Error("Invalid movie ID") }
            return
        }
        _movieId.value = movieId
        viewModelScope.launch {
            movieIdTrigger.emit(movieId)
        }
    }

    fun loadReviews(movieId: Int) {
        if (movieId <= 0) return
        viewModelScope.launch {
            reviewsTrigger.emit(movieId)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.update { true }
            refreshTrigger.emit(Unit)
        }
    }

    fun retryReviews() {
        viewModelScope.launch {
            retryReviewsTrigger.emit(Unit)
        }
    }

    fun retry() {
        _movieId.value?.let { loadMovieDetails(it) }
    }

    public override fun onCleared() {
        super.onCleared()
        _reviews.value = null
    }
}