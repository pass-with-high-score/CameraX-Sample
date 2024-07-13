package com.futureus.cameraxapp.domain.repository

import com.futureus.cameraxapp.data.dto.ImageDto

interface ImageRepository {
    suspend fun uploadImage(
        imageDto: ImageDto
    ): String
}