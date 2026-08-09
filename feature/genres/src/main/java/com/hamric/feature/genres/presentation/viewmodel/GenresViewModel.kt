package com.hamric.feature.genres.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamric.feature.genres.domain.usecase.GetGenresUseCase
import com.hamric.feature.genres.presentation.state.GenresUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GenresViewModel @Inject constructor(
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenresUiState>(GenresUiState.Loading)
    val uiState: StateFlow<GenresUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {
            refreshTrigger
                .onStart { emit(Unit) }
                .collectLatest {
                    loadGenres()
                }
        }
    }

    private suspend fun loadGenres() {
        _uiState.update { GenresUiState.Loading }

        val result = getGenresUseCase()
        result.fold(
            onSuccess = { genres ->
                _uiState.update {
                    if (genres.isEmpty()) {
                        GenresUiState.Empty
                    } else {
                        GenresUiState.Success(genres)
                    }
                }
            },
            onFailure = { exception ->
                Log.e("GenresViewModel", "Error loading genres", exception)
                _uiState.update {
                    GenresUiState.Error("Unable to load genres. Please check your connection.")
                }
            }
        )
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.update { true }
            refreshTrigger.emit(Unit)
            _isRefreshing.update { false }
        }
    }

    fun retry() = refresh()

    override fun onCleared() {
        super.onCleared()
    }
}