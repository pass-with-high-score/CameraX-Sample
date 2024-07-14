package com.futureus.cameraxapp.data.dto

data class VideoDto(
    val video: ByteArray,
    val fileName: String,
    val uri: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VideoDto

        if (!video.contentEquals(other.video)) return false
        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = video.contentHashCode()
        result = 31 * result + fileName.hashCode()
        return result
    }
}
