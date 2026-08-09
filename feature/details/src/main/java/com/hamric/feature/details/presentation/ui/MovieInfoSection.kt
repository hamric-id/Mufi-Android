package com.hamric.feature.details.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hamric.core.model.Movie

@Composable
fun MovieInfoSection(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                        contentDescription = movie.title,
                        modifier = Modifier
                            .size(100.dp, 150.dp)
                    )

                    Column(modifier = Modifier.padding(start = 16.dp)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = movie.releaseDate,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "⭐ ${movie.voteAverage}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = " (${movie.voteCount} votes)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}