package com.futureus.cameraxapp.presentation.home

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CameraScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient

@OptIn(UnstableApi::class)
@Destination<RootGraph>(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    videoDto: ResultRecipient<CameraScreenDestination, String>,
    modifier: Modifier = Modifier
) {

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val context = LocalContext.current


    var videoLink by remember {
        mutableStateOf("")
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        videoDto.onNavResult { result ->
            when (result) {
                NavResult.Canceled -> Log.d("HomeScreen", "Canceled")
                is NavResult.Value -> {
                    Log.d("HomeScreen", "Value: ${result.value}")
                    videoLink = result.value
                }
            }

        }

        if (videoLink.isNotEmpty()){
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = ExoPlayer.Builder(context).build().apply {
                            val mediaItem = MediaItem.fromUri(videoLink)
                            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                                .createMediaSource(mediaItem)
                            setMediaSource(mediaSource)
                            prepare()
                            playWhenReady = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f/9),
                update = { playerView ->
                    playerView.player = ExoPlayer.Builder(context).build().apply {
                        val mediaItem = MediaItem.fromUri(videoLink)
                        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                            .createMediaSource(mediaItem)
                        setMediaSource(mediaSource)
                        prepare()
                        playWhenReady = true
                    }
                }
            )
        }

        Button(
            onClick = {
                navigator.navigate(
                    CameraScreenDestination
                )
            }
        ) {
            Text("Camera")
        }
    }
}