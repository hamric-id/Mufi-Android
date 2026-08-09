package com.hamric.mufi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hamric.feature.details.presentation.ui.MovieDetailScreen
import com.hamric.feature.details.presentation.ui.YouTubePlayer
import com.hamric.feature.genres.presentation.ui.GenresScreen
import com.hamric.feature.movies.presentation.ui.MoviesScreen
import com.hamric.mufi.ui.theme.MufiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MufiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MufiNavigation()
                }
            }
        }
    }
}

@Composable
fun MufiNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "genres"
    ) {
        composable("genres") {
            GenresScreen(
                onGenreClick = { genre ->
                    navController.navigate("movies/${genre.id}/${genre.name}")
                }
            )
        }

        composable(
            route = "movies/{genreId}/{genreName}",
            arguments = listOf(
                navArgument("genreId") { type = NavType.IntType },
                navArgument("genreName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getInt("genreId") ?: 0
            val genreName = backStackEntry.arguments?.getString("genreName") ?: ""

            MoviesScreen(
                genreId = genreId,
                genreName = genreName,
                onMovieClick = { movie ->
                    navController.navigate("details/${movie.id}")
                }
            )
        }

        composable(
            route = "details/{movieId}",
            arguments = listOf(
                navArgument("movieId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0

            MovieDetailScreen(
                movieId = movieId,
                onBack = { navController.popBackStack() },
                onTrailerClick = { video ->
                    navController.navigate("trailer/${video.key}")
                }
            )
        }

        composable(
            route = "trailer/{videoKey}",
            arguments = listOf(
                navArgument("videoKey") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val videoKey = backStackEntry.arguments?.getString("videoKey") ?: ""
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                YouTubePlayer(
                    videoKey = videoKey,
                    modifier = Modifier.fillMaxSize()
                )

                androidx.compose.material3.IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(androidx.compose.ui.Alignment.TopStart)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }

    }
}