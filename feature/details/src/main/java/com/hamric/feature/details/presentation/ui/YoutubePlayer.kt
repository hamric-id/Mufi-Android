package com.hamric.feature.details.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YouTubePlayer(
    videoKey: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true
) {
    val context = LocalContext.current

    val youTubePlayerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
        }
    }

    DisposableEffect(videoKey) {
        val listener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                if (autoPlay) {
                    youTubePlayer.loadVideo(videoKey, 0f)
                } else {
                    youTubePlayer.cueVideo(videoKey, 0f)
                }
            }
        }

        youTubePlayerView.addYouTubePlayerListener(listener)
        youTubePlayerView.initialize(listener)

        onDispose {
            youTubePlayerView.removeYouTubePlayerListener(listener)
            youTubePlayerView.release()
        }
    }

    AndroidView(
        factory = { youTubePlayerView },
        modifier = modifier.fillMaxSize()
    )
}