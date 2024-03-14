//package com.example.myapplication.activities
//
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Environment
//import android.util.Log
//import android.view.View
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.camera.core.AspectRatio
//import androidx.camera.core.Camera
//import androidx.camera.core.CameraControl
//import androidx.camera.core.CameraSelector
//import androidx.camera.core.ImageCapture
//import androidx.camera.core.ImageCaptureException
//import androidx.camera.core.Preview
//import androidx.camera.core.UseCaseGroup
//import androidx.camera.lifecycle.ProcessCameraProvider
//import androidx.camera.view.PreviewView
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.LifecycleOwner
//import com.example.myapplication.R
//import com.example.myapplication.databinding.ActivityCameraViewBinding
//import com.google.common.util.concurrent.ListenableFuture
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Locale
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.ExecutorService
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.min
//
//
//class CameraViewActivity : AppCompatActivity() {
//    private var imageCapture: ImageCapture? = null
//    private lateinit var outputDirectory: File
//    private lateinit var cameraExecutor: ExecutorService
//
//    private var previewView: PreviewView? = null
//    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>? = null
//
//    @RequiresApi(Build.VERSION_CODES.R)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_camera_view)
//
//        previewView = findViewById(R.id.camera_view);
//        //cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//
//        //startCamera()
//
//        setCameraProviderListener();
//        //previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
////        cameraProviderFuture!!.addListener({
////            try {
////                val cameraProvider = cameraProviderFuture!!.get()
////                bindPreview(cameraProvider)
////            } catch (e: ExecutionException) {
////                e.printStackTrace()
////            } catch (e: InterruptedException) {
////                e.printStackTrace()
////            }
////        }, ContextCompat.getMainExecutor(this))
////        //TODO block if no permissions?
////
////        var btnSwitch = findViewById<ImageButton>(R.id.btn_switch)
////        var btnGallery = findViewById<ImageButton>(R.id.btn_gallery)
////        var btnPhoto = findViewById<ImageButton>(R.id.btn_photo)
////        val previewView = findViewById<PreviewView>(R.id.camera_view)
////
////        startCamera()
////
////        outputDirectory = getOutputDirectory()
////        Log.d(TAG, "Output file directory: ${outputDirectory.path}")
////
////        cameraExecutor = Executors.newSingleThreadExecutor()
////
////        btnSwitch.setOnClickListener { switchCamera() }
////        btnGallery.setOnClickListener { goToGallery() }
////        btnPhoto.setOnClickListener { takePhoto() }
//    }
//
////    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
////        val preview = Preview.Builder().build()
////        val cameraSelector = CameraSelector.Builder()
////            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
////            .build()
////        preview.setSurfaceProvider(binding.cameraView.surface)
////        val camera: Camera =
////            cameraProvider.bindToLifecycle((this as LifecycleOwner), cameraSelector, preview)
////    }
//    private fun setCameraProviderListener() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            try {
//                val cameraProvider = cameraProviderFuture.get()
//                bindPreview(cameraProvider)
//                Log.e(TAG, "did it")
//            } catch (e: ExecutionException) {
//                // No errors need to be handled for this Future
//                // This should never be reached
//                Log.e(TAG, e.printStackTrace().toString())
//            } catch (e: InterruptedException) {
//                Log.e(TAG, e.printStackTrace().toString())
//            }
//        }, ContextCompat.getMainExecutor(this))
//    }
//    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
//
//        val cameraSelector =
//            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//        val preview = Preview.Builder().build()
//        Log.e(TAG, "did it1")
//        preview.setSurfaceProvider(previewView!!.surfaceProvider)
//        val viewPort = previewView!!.viewPort
//        Log.e(TAG, "did it2")
//        if (viewPort != null) {
//            val useCaseGroup = UseCaseGroup.Builder()
//                .addUseCase(preview)
//                .addUseCase(imageCapture!!)
//                .setViewPort(viewPort)
//                .build()
//            Log.e(TAG, "did it3")
//            cameraProvider.unbindAll()
//            val camera: Camera = cameraProvider.bindToLifecycle(this, cameraSelector, useCaseGroup)
//            val cameraControl: CameraControl = camera.cameraControl
//            cameraControl.setLinearZoom(0.3.toFloat())
//        }
//        Log.e(TAG, "did it4")
//    }
//    private fun startCamera(){
//
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener(Runnable {
//
//            val cameraProvider = cameraProviderFuture.get()
//
//            val preview : Preview = Preview.Builder().build().also {
//                it.setSurfaceProvider(ActivityCameraViewBinding.inflate(layoutInflater).cameraView.surfaceProvider)
//            }
//            val cameraSelector : CameraSelector = CameraSelector.Builder()
//                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .build()
//
//
//
//            var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
//
////            val binding = ActivityCameraViewBinding.inflate(layoutInflater)
////            val preview = Preview.Builder().build().also {
////                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
////            }
////
////            val cameraSelector = CameraSelector.Builder()
////                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
////                .build()
////            Log.d(TAG, "here3")
////            try{
////                cameraProvider.unbindAll()
////                cameraProvider.bindToLifecycle(this, cameraSelector, preview)
////                Log.d(TAG, "here4")
////
////            }catch (exc: Exception) {
////                Log.e(TAG, "Use case binding failed", exc)
////            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun switchCamera(){
//
//    }
//    private fun goToGallery(){
//
//    }
//    @RequiresApi(Build.VERSION_CODES.R)
//    private fun takePhoto() {
////        val metrics = windowManager.currentWindowMetrics.bounds
////        Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")
////        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
////        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")
////        val rotation = fragmentCameraBinding.viewFinder.display.rotation
//        val imageCapture = imageCapture ?: return
//        // stable reference of the modifiable image capture use case
//        //val imageCapture = ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
//
//        // time-stamp the output file to save the image
//        val photoFile = File(
//            outputDirectory,
//            SimpleDateFormat(FILENAME_FORMAT, Locale.UK).format(System.currentTimeMillis()) + ".jpg"
//        )
//
//        // create output options object with file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
//
//
//        // set up image capture listener, which is triggered after photo has been taken
//        imageCapture.takePicture(
//            outputOptions,
//            ContextCompat.getMainExecutor(this),
//            object : ImageCapture.OnImageSavedCallback {
//                override fun onError(exc: ImageCaptureException) {
//                    Log.d(TAG, "Photo capture failed: ${exc.message}", exc)
//                }
//
//                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
//                    val savedUri = Uri.fromFile(photoFile)
//
//                    // set the saved uri to the image view
//                    findViewById<ImageView>(R.id.iv_capture).visibility = View.VISIBLE
//                    findViewById<ImageView>(R.id.iv_capture).setImageURI(savedUri)
//
//                    val msg = "Photo capture succeeded: $savedUri"
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
//                    Log.d(TAG, msg)
//                }
//            })
//    }
//
//    // creates a folder inside internal storage
//    private fun getOutputDirectory(): File {
//        val directory = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//            "WSP_Analyzer"
//        )
//
//        if (!directory.exists()) { directory.mkdirs() }
//
//        return if (directory != null && directory.exists())
//            directory else filesDir
//    }
//
//    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
//        return AspectRatio.RATIO_16_9
//    }
//    companion object {
//        private const val TAG = "Log Debug"
//        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
//        private const val RATIO_4_3_VALUE = 4.0 / 3.0
//        private const val RATIO_16_9_VALUE = 16.0 / 9.0
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cameraExecutor.shutdown()
//    }
//}
