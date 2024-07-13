package com.futureus.cameraxapp.presentation

import android.net.Uri
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futureus.cameraxapp.data.dto.ImageDto
import com.futureus.cameraxapp.data.dto.VideoDto
import com.futureus.cameraxapp.domain.repository.CameraRepository
import com.futureus.cameraxapp.domain.repository.ImageRepository
import com.futureus.cameraxapp.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val cameraRepository: CameraRepository,
    private val imageRepository: ImageRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _uri = MutableStateFlow<Uri?>(null)
    val uri = _uri.asStateFlow()

    private val _imageDto = MutableStateFlow<ImageDto?>(null)
    val imageDto = _imageDto.asStateFlow()

    private val _videoDto = MutableStateFlow<VideoDto?>(null)
    val videoDto = _videoDto.asStateFlow()


    private val _isTakingPhoto = MutableStateFlow(false)
    val isTakingPhoto = _isTakingPhoto.asStateFlow()

    private val _timer = MutableStateFlow(0)
    val timer = _timer.asStateFlow()

    fun onTakePhoto(
        controller: LifecycleCameraController
    ) {
        viewModelScope.launch {
            _isTakingPhoto.update { true }
            _imageDto.update {
                cameraRepository.takePhoto(controller)
            }
            _isTakingPhoto.update { false }

            // upload image to supabase
            _imageDto.value?.let {
                viewModelScope.launch {
                    imageRepository.uploadImage(imageDto = _imageDto.value!!)
                }
            }

        }
    }

    fun onRecordVideo(
        controller: LifecycleCameraController
    ) {
        _isRecording.update { !isRecording.value }
        viewModelScope.launch {
            _videoDto.update {
                cameraRepository.recordVideo(controller)
            }
            Log.d("CameraViewModel", "Uri: ${uri.value}")

            // upload video to supabase
            _videoDto.value?.let {
                viewModelScope.launch {
                    videoRepository.uploadVideo(videoDto = _videoDto.value!!)
                }
            }

        }
    }


}