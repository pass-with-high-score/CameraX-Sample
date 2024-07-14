package com.futureus.cameraxapp.data.repository

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.content.ContextCompat
import com.futureus.cameraxapp.R
import com.futureus.cameraxapp.data.dto.ImageDto
import com.futureus.cameraxapp.data.dto.VideoDto
import com.futureus.cameraxapp.domain.repository.CameraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresApi(Build.VERSION_CODES.Q)
class CameraRepositoryImpl @Inject constructor(
    private val application: Application
) : CameraRepository {

    private var recording: Recording? = null

    override suspend fun takePhoto(controller: LifecycleCameraController): ImageDto {
        return suspendCancellableCoroutine { continuation ->
            controller.takePicture(
                ContextCompat.getMainExecutor(application),
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {
                        super.onCaptureSuccess(image)

                        val matrix = Matrix().apply {
                            postRotate(
                                image.imageInfo.rotationDegrees.toFloat()
                            )
                        }

                        val imageBitmap = Bitmap.createBitmap(
                            image.toBitmap(),
                            0, 0,
                            image.width, image.height,
                            matrix, true
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val uri = savePhoto(imageBitmap)
                                withContext(Dispatchers.Main) {
                                    continuation.resume(uri)
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Log.e("CameraRepositoryImpl", "Error saving photo: ${e.message}")
                                }
                            }
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                       Log.e("CameraRepositoryImpl", "Error taking photo: ${exception.message}")
                    }
                }
            )
        }
    }

    private suspend fun savePhoto(bitmap: Bitmap): ImageDto {
        return withContext(Dispatchers.IO) {
            val resolver: ContentResolver = application.contentResolver

            val imageCollection = MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )

            val appName = application.getString(R.string.app_name)
            val timeInMillis = System.currentTimeMillis()

            val imageContentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "${timeInMillis}_image" + ".jpg"
                )
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + "/$appName"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.DATE_TAKEN, timeInMillis)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            val imageMediaStoreUri: Uri? = resolver.insert(
                imageCollection,
                imageContentValues
            )

            imageMediaStoreUri?.let { uri ->
                try {
                    resolver.openOutputStream(uri)?.let { outputStream ->
                        bitmap.compress(
                            Bitmap.CompressFormat.JPEG, 100, outputStream
                        )
                    }

                    imageContentValues.clear()
                    imageContentValues.put(
                        MediaStore.MediaColumns.IS_PENDING, 0
                    )
                    resolver.update(
                        uri,
                        imageContentValues,
                        null, null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
                Log.d("CameraRepositoryImpl", "Uri: $uri")
                uri
            } ?: Uri.EMPTY

            // convert bitmap to byte array
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()

            val imageDto = ImageDto(
                image = byteArray,
                fileName = "${timeInMillis}_image.jpg"
            )
            imageDto
        }
    }

    override suspend fun recordVideo(controller: LifecycleCameraController): VideoDto =
        suspendCancellableCoroutine { continuation ->
            if (ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Check if there's an ongoing recording
                if (recording != null) {
                    recording?.stop()
                    recording = null
                    Log.d(
                        "CameraRepositoryImpl",
                        "A recording is already in progress. Stopping the current recording."
                    )
                    return@suspendCancellableCoroutine
                }

                val timeInMillis = System.currentTimeMillis()
                val file = File(application.filesDir, "${timeInMillis}_video.mp4")

                recording = controller.startRecording(
                    FileOutputOptions.Builder(file).build(),
                    AudioConfig.create(true),
                    ContextCompat.getMainExecutor(application)
                ) { event ->
                    when (event) {
                        is VideoRecordEvent.Finalize -> {
                            if (!event.hasError()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        saveVideo(file)
                                        val videoDto = VideoDto(
                                            video = file.readBytes(),
                                            fileName = file.name
                                        )
                                        continuation.resume(videoDto)
                                    } catch (e: Exception) {
                                        continuation.resumeWithException(e)
                                    } finally {
                                        recording = null
                                    }
                                }
                            } else {
                                val error = event.error
                                Log.e("CameraRepositoryImpl", "Recording failed with error: $error")
                            }
                        }

                        else -> {
                            Log.d("CameraRepositoryImpl", "Recording event: $event")
                        }
                    }
                }
            } else {
                Log.e("CameraRepositoryImpl", "Permission to record audio is not granted")
            }
        }

    private suspend fun saveVideo(file: File): Uri {
        return withContext(Dispatchers.IO) {
            val resolver: ContentResolver = application.contentResolver

            val videoCollection = MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )

            val appName = application.getString(R.string.app_name)
            val timeInMillis = System.currentTimeMillis()

            val videoContentValues = ContentValues().apply {
                put(
                    MediaStore.Video.Media.DISPLAY_NAME,
                    file.name
                )
                put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_DCIM + "/$appName"
                )
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.DATE_ADDED, timeInMillis / 1000)
                put(MediaStore.Video.Media.DATE_MODIFIED, timeInMillis / 1000)
                put(MediaStore.Video.Media.DATE_TAKEN, timeInMillis)
                put(MediaStore.Video.Media.IS_PENDING, 1)
            }

            val videoMediaStoreUri: Uri? = resolver.insert(
                videoCollection,
                videoContentValues
            )

            videoMediaStoreUri?.let { uri ->
                try {
                    resolver.openOutputStream(uri)?.let { outputStream ->
                        file.inputStream().use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    videoContentValues.clear()
                    videoContentValues.put(
                        MediaStore.Video.Media.IS_PENDING, 0
                    )
                    resolver.update(
                        uri,
                        videoContentValues,
                        null, null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    resolver.delete(uri, null, null)
                }
                uri
            } ?: Uri.EMPTY
        }
    }
}
