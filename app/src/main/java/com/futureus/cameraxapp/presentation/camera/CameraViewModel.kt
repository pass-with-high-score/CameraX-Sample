package com.futureus.cameraxapp.presentation.camera

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
import kotlinx.coroutines.delay
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

    private val _imageLink = MutableStateFlow<String?>(null)
    val imageLink = _imageLink.asStateFlow()

    private val _videoDto = MutableStateFlow<VideoDto?>(null)
    val videoDto = _videoDto.asStateFlow()

    private val _videoLink = MutableStateFlow<VideoDto?>(null)
    val videoLink = _videoLink.asStateFlow()


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
                    _imageLink.update {
                        imageRepository.uploadImage(imageDto = _imageDto.value!!)
                    }
                    Log.d("CameraViewModel", "Image Link: ${imageLink.value}")
                }
            }

        }
    }

    fun onRecordVideo(
        controller: LifecycleCameraController
    ) {
        _isRecording.update { !isRecording.value }

        // start recording video

        viewModelScope.launch {
            _videoDto.update {
                cameraRepository.recordVideo(controller)
            }
            // upload video to supabase
            _videoDto.value?.let {
                viewModelScope.launch {
                    _videoLink.update {
                        videoRepository.uploadVideo(videoDto = _videoDto.value!!)
                    }
                    Log.d("CameraViewModel", "Video Link: ${videoLink.value}")
                }
            }

        }
    }



}