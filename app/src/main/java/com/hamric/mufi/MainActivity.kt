package com.hamric.mufi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                    println("navigate to details movie '${movie.title}")
//                    navController.navigate("details/${movie.id}")
                }
            )
        }

    }
}