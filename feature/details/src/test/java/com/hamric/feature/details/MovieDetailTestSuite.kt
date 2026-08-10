package com.hamric.feature.details

import com.hamric.feature.details.data.paging.ReviewPagingSourceTest
import com.hamric.feature.details.data.repository.MovieDetailRepositoryImplTest
import com.hamric.feature.details.domain.usecase.GetMovieDetailsUseCaseTest
import com.hamric.feature.details.domain.usecase.GetMovieReviewsUseCaseTest
import com.hamric.feature.details.domain.usecase.GetMovieTrailerUseCaseTest
import com.hamric.feature.details.presentation.viewmodel.MovieDetailViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    MovieDetailRepositoryImplTest::class,
    GetMovieDetailsUseCaseTest::class,
    GetMovieReviewsUseCaseTest::class,
    GetMovieTrailerUseCaseTest::class,
    MovieDetailViewModelTest::class,
    ReviewPagingSourceTest::class
)
class MovieDetailTestSuite