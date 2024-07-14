package com.futureus.cameraxapp.presentation.camera

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.futureus.cameraxapp.MainActivity
import com.futureus.cameraxapp.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.generated.destinations.PreviewScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Composable
@Destination<RootGraph>
fun CameraScreen(
    navigator: DestinationsNavigator,
    resultNavigator: ResultBackNavigator<String>
) {
    val context  = LocalContext.current
    val controller = remember {
        LifecycleCameraController(
            context.applicationContext
        ).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    val cameraViewModel = hiltViewModel<CameraViewModel>()
    val isRecording by cameraViewModel.isRecording.collectAsState()
    val videoDto by cameraViewModel.videoLink.collectAsState()

    LaunchedEffect(videoDto) {
        if (videoDto != null) {
//            navigator.navigate(
//                PreviewScreenDestination(
//                    videoDto = videoDto!!.uri!!
//                )

//            )
            resultNavigator.navigateBack(videoDto!!.uri!!)
            println(videoDto!!.uri)
            println(videoDto!!.video)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ){
        val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PreviewView(it).apply {
                    this.controller = controller
                    controller.bindToLifecycle(lifecycleOwner)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 40.dp)
                .align(Alignment.TopCenter),
        ) {

            Box (
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(Color.White.copy(
                        alpha = 0.2f
                    )),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.open_gallery),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(26.dp)
                        .clickable {
                            navigator.navigateUp()
                        }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp)
                    .background(Color.White.copy(
                        alpha = 0.2f
                    ))
                    .clickable {
                        controller.cameraSelector =
                            if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
                                // stop recording
                                if (isRecording) {
                                    cameraViewModel.onRecordVideo(controller)
                                }
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            } else {
                                // stop recording
                                if (isRecording) {
                                    cameraViewModel.onRecordVideo(controller)
                                }
                                CameraSelector.DEFAULT_BACK_CAMERA
                            }
                    },
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = stringResource(R.string.switch_camera),
                        tint = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.8f
                        ),
                        modifier = Modifier.size(26.dp)
                    )
                }
            )

        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .size(45.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if (isRecording) {
                            cameraViewModel.onRecordVideo(controller)
                        }
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                "content://media/internal/images/media"
                            )
                        ).also {
                            context.startActivity(it)
                        }
                    },
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = stringResource(R.string.open_gallery),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.width(1.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable {
                        if((context as MainActivity).arePermissionsGranted()) {
                            cameraViewModel.onRecordVideo(controller)
                        }
                    },
                contentAlignment = Alignment.Center,
                content = {
                    Icon(
                        imageVector =
                        if (isRecording) Icons.Default.Stop
                        else Icons.Default.Videocam,
                        contentDescription = stringResource(R.string.record_video),
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            )

            Spacer(modifier = Modifier.width(1.dp))

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(60.dp)
                    .clickable {
                        if((context as MainActivity).arePermissionsGranted()) {
                            if (isRecording) {
                                cameraViewModel.onRecordVideo(controller)
                                Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show()
                            }else {
                                cameraViewModel.onTakePhoto(controller)
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
                content = {
//                    Icon(
//                        imageVector = Icons.Default.PhotoLibrary,
//                        contentDescription = stringResource(R.string.take_photo),
//                        tint = MaterialTheme.colorScheme.onPrimary,
//                        modifier = Modifier.size(26.dp)
//                    )

                }
            )

        }
    }
}