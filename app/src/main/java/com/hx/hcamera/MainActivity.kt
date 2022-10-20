package com.hx.hcamera

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.hx.hcamera.databinding.ActivityMainBinding
import java.lang.reflect.Executable
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author huangxuan
 * @since 2022/10/20 11:01
 * QQ: 1360643904
 * CameraX 相机主页
 */
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var imageCapture: ImageCapture
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.bind(
            LayoutInflater.from(this).inflate(R.layout.activity_main, null)
        )
        setContentView(binding.root)
        initCamera()
        initController()
    }

    // 初始化控制器按钮
    private fun initController() {
        val imageCapture = imageCapture ?: return
        val name = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputFileOption = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()
        // 拍摄按钮
        binding.btnShoot.setOnClickListener {
            Log.d("hcamera", imageCapture?.toString() ?: "空的")
            imageCapture.takePicture(
                outputFileOption,
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                })
        }
        // 旋转镜头
        binding.tvChangeCamera.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }
            startCamera()
            initController()
        }
        // 高级模式
        binding.tvPro.setOnClickListener {
            startActivity(Intent(this, PopularCameraActivity::class.java))
        }
    }

    // 初始化相机，查看权限
    private fun initCamera() {
        if (allPermissionsGranted()) {
            startCamera()
        }
    }

    // 开启相机
    private fun startCamera() {
        imageCapture = ImageCapture.Builder().build()
        val processCameraProvider = ProcessCameraProvider.getInstance(this)
        processCameraProvider.addListener({
            val preview = Preview.Builder().build()
                .also {
                    it.setSurfaceProvider(binding.preview.surfaceProvider)
                }
            processCameraProvider.get().apply {
                try {
                    unbindAll()
                    bindToLifecycle(
                        this@MainActivity,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.d("hcamera", e.message ?: "")
                }
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // 确认所有权限都注册
    private fun allPermissionsGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        // 权限列表
        val PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
    }
}