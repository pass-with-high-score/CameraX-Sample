package com.futureus.cameraxapp.presentation.preview

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

@OptIn(UnstableApi::class)
@Destination<RootGraph>
@Composable
fun PreviewScreen(
    modifier: Modifier = Modifier,
    videoDto: String,
) {

    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }

    val context = LocalContext.current

    val mediaItem = MediaItem.fromUri(videoDto)

    val mediaSource: MediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
        .createMediaSource(mediaItem)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaSource(mediaSource)
            prepare()
            playWhenReady = true
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
            factory = {
                PlayerView(context).also { playerView ->
                    playerView.player = exoPlayer
                }
            },
            update = {
                when (lifecycle) {
                    Lifecycle.Event.ON_RESUME -> {
                        it.onPause()
                        it.player?.pause()
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        it.onResume()
                    }

                    else -> Unit
                }
            }
        )

        // Thumbnail of the video
        val model = ImageRequest.Builder(context)
            .data(videoDto)
            .videoFrameMillis(10000)
            .decoderFactory { result, options, _ ->
                VideoFrameDecoder(
                    result.source,
                    options
                )
            }
            .build()

        AsyncImage(
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16f / 9f),
            model = model,
            contentDescription = "video thumbnail",
            contentScale = ContentScale.Crop
        )
    }


}