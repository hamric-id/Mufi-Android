package com.hamric.feature.movies

import com.hamric.feature.movies.data.paging.MoviePagingSourceTest
import com.hamric.feature.movies.data.repository.MovieRepositoryImplTest
import com.hamric.feature.movies.domain.usecase.GetMoviesByGenreUseCaseTest
import com.hamric.feature.movies.presentation.viewmodel.MoviesViewModelTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    MovieRepositoryImplTest::class,
    GetMoviesByGenreUseCaseTest::class,
    MoviesViewModelTest::class,
    MoviePagingSourceTest::class
)
class MoviesTestSuite