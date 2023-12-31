package com.wadud.facedetection.viewmodel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceAnalyzerViewModel @Inject constructor(private val faceAnalyzer: FaceAnalyzer) : ViewModel(){
    private val _faceDetectedState: MutableStateFlow<String> = MutableStateFlow("Place your face within the camera range")
    val faceDetected: StateFlow<String> = _faceDetectedState.asStateFlow()

    fun processImage(imageProxy: ImageProxy){
        viewModelScope.launch (Dispatchers.IO){
            faceAnalyzer.analyze(imageProxy){
                _faceDetectedState.value = it
            }
        }
    }
}