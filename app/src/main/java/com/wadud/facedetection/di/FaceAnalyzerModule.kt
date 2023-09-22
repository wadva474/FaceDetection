package com.wadud.facedetection.di

import com.wadud.facedetection.viewmodel.FaceAnalyzerCallback
import com.wadud.facedetection.viewmodel.FaceAnalyzerCallbackImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class  FaceAnalyzerModule {

    @Binds
    abstract fun bindsFaceAnalyzerCallback(faceAnalyzerCallbackImpl: FaceAnalyzerCallbackImpl) : FaceAnalyzerCallback
}