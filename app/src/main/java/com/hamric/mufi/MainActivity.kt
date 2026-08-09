package com.hamric.mufi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hamric.feature.genres.presentation.ui.GenresScreen
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
                    println("navigate to list movie with genre ${genre.name}")
//                    navController.navigate("movies/${genre.id}/${genre.name}")
                }
            )
        }

    }
}