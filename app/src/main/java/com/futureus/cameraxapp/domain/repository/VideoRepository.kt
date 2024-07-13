package com.futureus.cameraxapp.domain.repository

import com.futureus.cameraxapp.data.dto.VideoDto

interface VideoRepository {
    suspend fun uploadVideo(
        videoDto: VideoDto
    ): String
}