package com.futureus.cameraxapp.di

import com.futureus.cameraxapp.data.repository.CameraRepositoryImpl
import com.futureus.cameraxapp.data.repository.ImageRepositoryImpl
import com.futureus.cameraxapp.data.repository.VideoRepositoryImpl
import com.futureus.cameraxapp.domain.repository.CameraRepository
import com.futureus.cameraxapp.domain.repository.ImageRepository
import com.futureus.cameraxapp.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CameraRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCameraRepository(
        cameraRepositoryImpl: CameraRepositoryImpl
    ): CameraRepository

    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        videoRepositoryImpl: VideoRepositoryImpl
    ): VideoRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

}