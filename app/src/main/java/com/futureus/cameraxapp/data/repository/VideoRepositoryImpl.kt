package com.futureus.cameraxapp.data.repository

import com.futureus.cameraxapp.BuildConfig
import com.futureus.cameraxapp.data.dto.VideoDto
import com.futureus.cameraxapp.domain.repository.VideoRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.UploadStatus
import io.github.jan.supabase.storage.uploadAsFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

class VideoRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : VideoRepository {
    override suspend fun uploadVideo(videoDto: VideoDto): String {
        if (videoDto.video.isNotEmpty()) {
            val videoUrl = storage.from("image")

            videoUrl.uploadAsFlow(
                path = videoDto.fileName,
                data = videoDto.video,
                upsert = true
            ).collect{
                when(it) {
                    is UploadStatus.Progress -> println("Progress: ${it.totalBytesSend.toFloat() / it.contentLength * 100}%")
                    is UploadStatus.Success -> println("Success")
                }
            }

            val url = videoUrl.createSignedUrl(path = videoDto.fileName, expiresIn = 1000.minutes)

            println("Video URL: $url")

            return buildVideoUrl(videoDto.fileName)
        } else {
            throw IllegalArgumentException("Video is empty")
        }
    }

    private fun buildVideoUrl(videoFileName: String) =
        "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/${videoFileName}".replace(" ", "%20")

}