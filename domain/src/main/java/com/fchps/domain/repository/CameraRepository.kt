package com.fchps.domain.repository


interface CameraRepository {
    fun initializeCamera()
    fun captureImage(onImageCaptured: (String) -> Unit, onError: (String) -> Unit)
    fun toggleFlash()
    fun releaseCamera()
    fun switchCamera()
}