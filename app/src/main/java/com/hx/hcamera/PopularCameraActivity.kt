package com.hx.hcamera

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.hx.hcamera.databinding.ActivityPopularCameraBinding

class PopularCameraActivity : AppCompatActivity() {
    lateinit var binding: ActivityPopularCameraBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopularCameraBinding.bind(
            LayoutInflater.from(this).inflate(R.layout.activity_popular_camera, null)
        )
        setContentView(binding.root)
        initBackCamera()
        initFrontCamera()
    }

    private fun initFrontCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewFront.surfaceProvider)
            }
            processCameraProvider.get().apply {
                unbindAll()
                bindToLifecycle(
                    this@PopularCameraActivity,
                    CameraSelector.DEFAULT_FRONT_CAMERA,
                    preview
                )
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun initBackCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewBack.surfaceProvider)
            }
            processCameraProvider.get().apply {
                unbindAll()
                bindToLifecycle(
                    this@PopularCameraActivity,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            }
        }, ContextCompat.getMainExecutor(this))
    }
}