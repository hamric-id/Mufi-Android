package com.hamric.feature.details.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.hamric.core.model.Video
import com.hamric.feature.details.presentation.state.MovieDetailUiState
import com.hamric.feature.details.presentation.state.ReviewsState
import com.hamric.feature.details.presentation.viewmodel.MovieDetailViewModel
import com.hamric.feature.movies.presentation.ui.components.ErrorBanner
import com.hamric.feature.movies.presentation.ui.components.ErrorState
import com.hamric.feature.movies.presentation.ui.components.LoadingIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onTrailerClick: (Video) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val reviewsState by viewModel.reviewsState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pagingItems = reviews?.collectAsLazyPagingItems()

    LaunchedEffect(movieId) {
        viewModel.loadMovieDetails(movieId)
        viewModel.loadReviews(movieId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
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
            when (val state = uiState) {
                is MovieDetailUiState.Loading -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }

                is MovieDetailUiState.Success -> {
                    MovieDetailContent(
                        movie = state.movie,
                        trailer = state.trailer,
                        reviewsState = reviewsState,
                        pagingItems = pagingItems,
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refresh,
                        onRetryReviews = viewModel::retryReviews,
                        onTrailerClick = onTrailerClick,
                        errorMessage = null
                    )
                }

                is MovieDetailUiState.SuccessWithError -> {
                    MovieDetailContent(
                        movie = state.movie,
                        trailer = state.trailer,
                        reviewsState = reviewsState,
                        pagingItems = pagingItems,
                        isRefreshing = isRefreshing,
                        onRefresh = viewModel::refresh,
                        onRetryReviews = viewModel::retryReviews,
                        onTrailerClick = onTrailerClick,
                        errorMessage = state.message
                    )
                }

                is MovieDetailUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.retry() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: com.hamric.core.model.Movie,
    trailer: Video?,
    reviewsState: ReviewsState,
    pagingItems: androidx.paging.compose.LazyPagingItems<com.hamric.core.model.Review>?,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onRetryReviews: () -> Unit,
    onTrailerClick: (Video) -> Unit,
    errorMessage: String? = null
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (errorMessage != null) {
                item {
                    ErrorBanner(
                        message = errorMessage
                    )
                }
            }

            item {
                MovieInfoSection(movie = movie)
            }

            item {
                MovieTrailerSection(
                    trailer = trailer,
                    onTrailerClick = onTrailerClick
                )
            }

            item {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when (reviewsState) {
                is ReviewsState.Loading -> {
                    item {
                        LoadingIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 5.dp)
                        )
                    }
                }
                is ReviewsState.Error -> {
                    item {
                        ErrorState(
                            message = reviewsState.message,
                            onRetry = onRetryReviews,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                is ReviewsState.Success -> {
                    if (pagingItems != null) {
                        items(
                            count = pagingItems.itemCount,
                            key = { index -> pagingItems[index]?.id ?: index }
                        ) { index ->
                            val review = pagingItems[index]
                            review?.let {
                                ReviewItem(
                                    review = it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 3.dp)
                                )
                            }
                        }

                        if (pagingItems.loadState.append is LoadState.Loading) {
                            item {
                                LoadingIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }

                        if (pagingItems.loadState.append is LoadState.Error) {
                            item {
                                ErrorState(
                                    message = "Failed to load more reviews",
                                    onRetry = { pagingItems.retry() },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}