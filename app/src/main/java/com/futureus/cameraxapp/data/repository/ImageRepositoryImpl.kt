package com.futureus.cameraxapp.data.repository

import com.futureus.cameraxapp.BuildConfig
import com.futureus.cameraxapp.data.dto.ImageDto
import com.futureus.cameraxapp.domain.repository.ImageRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.upload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : ImageRepository {
    override suspend fun uploadImage(imageDto: ImageDto): String {
        withContext(Dispatchers.IO){
            if (imageDto.image.isNotEmpty()){
                val imageUrl = storage.from("Product%20Image").upload(
                    path = imageDto.fileName,
                    data = imageDto.image,
                    upsert = true
                )

                return@withContext buildImageUrl(imageDto.fileName)
            } else {
                throw IllegalArgumentException("Image is empty")
            }
        }

        return ""
    }

    // Because I named the bucket as "Product Image" so when it turns to an url, it is "%20"
    // For better approach, you should create your bucket name without space symbol
    private fun buildImageUrl(imageFileName: String) =
        "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/${imageFileName}".replace(" ", "%20")
}