package com.futureus.cameraxapp.data.dto

data class ImageDto(
    val image: ByteArray,
    val fileName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageDto

        if (!image.contentEquals(other.image)) return false
        if (fileName != other.fileName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = image.contentHashCode()
        result = 31 * result + fileName.hashCode()
        return result
    }
}
