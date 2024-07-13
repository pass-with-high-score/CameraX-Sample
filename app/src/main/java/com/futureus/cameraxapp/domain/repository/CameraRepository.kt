package com.futureus.cameraxapp.domain.repository

import android.net.Uri
import androidx.camera.view.LifecycleCameraController
import com.futureus.cameraxapp.data.dto.ImageDto
import com.futureus.cameraxapp.data.dto.VideoDto

interface CameraRepository {
    suspend fun takePhoto(
        controller: LifecycleCameraController
    ): ImageDto

    suspend fun recordVideo(
        controller: LifecycleCameraController
    ): VideoDto
}