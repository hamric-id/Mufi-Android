package com.hamric.feature.movies.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamric.core.model.Movie
import com.hamric.feature.movies.presentation.ui.components.ErrorState
import com.hamric.feature.movies.presentation.ui.components.LoadingIndicator
import com.hamric.feature.movies.presentation.ui.components.MovieList
import com.hamric.feature.movies.presentation.ui.state.MoviesUiState
import com.hamric.feature.movies.presentation.viewmodel.MoviesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    genreId: Int,
    genreName: String,
    onBack: () -> Unit,
    viewModel: MoviesViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(genreId) {
        viewModel.loadMovies(genreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$genreName Movies") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is MoviesUiState.Idle -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                is MoviesUiState.Loading -> {
                    if (movies == null) {
                        LoadingIndicator(modifier = Modifier.fillMaxSize())
                    } else {
                        MovieList(
                            movies = movies,
                            onMovieClick = onMovieClick,
                            isRefreshing = true,
                            onRefresh = viewModel::refresh,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is MoviesUiState.Success -> {
                    MovieList(
                        movies = movies,
                        onMovieClick = onMovieClick,
                        isRefreshing = false,
                        onRefresh = viewModel::refresh,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is MoviesUiState.Error -> {
                    if (movies != null) {
                        MovieList(
                            movies = movies,
                            onMovieClick = onMovieClick,
                            isRefreshing = false,
                            onRefresh = viewModel::refresh,
                            modifier = Modifier.fillMaxSize(),
                            errorMessage = (uiState as? MoviesUiState.Error)?.message
                        )
                    } else {
                        ErrorState(
                            message = (uiState as? MoviesUiState.Error)?.message?:"Failed to Fetch",
                            onRetry = viewModel::retry,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}