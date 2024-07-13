package com.futureus.cameraxapp.data.repository

import com.futureus.cameraxapp.BuildConfig
import com.futureus.cameraxapp.data.dto.VideoDto
import com.futureus.cameraxapp.domain.repository.VideoRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : VideoRepository {
    override suspend fun uploadVideo(videoDto: VideoDto): String {
        if (videoDto.video.isNotEmpty()) {
            val videoUrl = storage.from("Product%20Image").upload(
                path = videoDto.fileName,
                data = videoDto.video,
                upsert = true
            )

            return buildVideoUrl(videoDto.fileName)
        } else {
            throw IllegalArgumentException("Video is empty")
        }
    }

    private fun buildVideoUrl(videoFileName: String) =
        "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/${videoFileName}".replace(" ", "%20")

}