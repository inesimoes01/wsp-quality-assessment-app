package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Classes.Settings
import com.example.myapplication.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry


class ResultsActivity: AppCompatActivity() {

    private var isImageExpanded = false
    private var imageFinalBitmap : Bitmap? = null

    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get image uri sent from the camera view activity
        val imageUri = intent.getStringExtra("uri")
        val contentUri = Uri.parse(imageUri)
        val filePath = getContentUriFilePath(this, contentUri)

        // encode image to send over json file
        val bitmap = BitmapFactory.decodeFile(filePath)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // get settings values
        sendRequest(encodedImage, Settings.getReal_width(), Settings.getReal_height(), Settings.isIsAI())

        setContentView(R.layout.activity_results)

        val imageOriginalView = findViewById<ImageButton>(R.id.original_image)
//        val imageExpandedView = findViewById<ImageView>(R.id.expanded_image)
//
//
//        imageOriginalView.setOnClickListener {
//            Log.d("rest api", "image clicked to zoom in")
//            if (!isImageExpanded){
//                isImageExpanded = true
//                imageExpandedView.visibility = View.VISIBLE
//            }
//        }
//
//        imageExpandedView.setOnClickListener{
//            Log.d("rest api", "image clicked to zoom out")
//
//            if (isImageExpanded) {
//                isImageExpanded = false
//                imageExpandedView.visibility = View.INVISIBLE;
//            }
//        }
    }

    private fun sendRequest(encodedImage: String?, width : Double, height : Double, isAI: Boolean) {
        val client = OkHttpClient()
        val url = "http://192.168.60.1:5000/perform_segmentation"

        // create the json file
        val jsonObject = JSONObject().apply {
            put("image_uri", encodedImage)
            put("width", width)
            put("height", height)
            put("isAI", isAI)
        }
        val body = jsonObject.toString().toRequestBody("application/json".toMediaType())

        // create the request to send
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // send request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException){
                Log.d("rest api", e.toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response){
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        responseData?.let {

                            handleJSONResponse(responseData)
                        }
                    }
                } else {
                    // Handle the error
                    Log.d("rest api", response.message)

                }
            }
        })
    }

    private fun handleJSONResponse(responseData : String){
        val jsonObject = JSONObject(responseData)

        // set image
        val imageBitmap = jsonObject.getString("image_bitmap")
        val imageBytes = Base64.decode(imageBitmap, Base64.DEFAULT)
        imageFinalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        findViewById<ImageView>(R.id.original_image).setImageBitmap(imageFinalBitmap)
        //findViewById<ImageView>(R.id.expanded_image).setImageBitmap(imageFinalBitmap)
        // read and write statistics values
        val vmd = jsonObject.getDouble("vmd")
        val rsf = jsonObject.getDouble("rsf")
        val coveragePercentage = jsonObject.getDouble("coverage_percentage")
        val numberDroplets = jsonObject.getInt("number_droplets")
        val overlappedPercentage = jsonObject.getDouble("overlapped_percentage")

        findViewById<TextView>(R.id.coverage_percentage_value).text = coveragePercentage.toString()
        findViewById<TextView>(R.id.rsf_value).text = rsf.toString()
        findViewById<TextView>(R.id.vmd_value).text = vmd.toString()
        findViewById<TextView>(R.id.nodroplets_value).text = numberDroplets.toString()
        findViewById<TextView>(R.id.overlapped_percentage_value).text = overlappedPercentage.toString()

        // create graph with the value of the radius
        val radiusArray = jsonObject.getJSONArray("values_of_radius")
        val radiusValues = mutableListOf<Double>()
        for (i in 0 until radiusArray.length()) {
            radiusValues.add(radiusArray.getDouble(i))
        }
        //TODO create a graph for radius



    }

    private fun expandImage(imageView: ImageView, captionTextView: TextView) {
        val scaleAnimation = ScaleAnimation(
            1.0f, 2.0f,  // Start and end values for the X axis scaling
            1.0f, 2.0f,  // Start and end values for the Y axis scaling
            Animation.ABSOLUTE, 0.5f,  // Pivot point of X scaling
            Animation.ABSOLUTE, 0.5f
        ) // Pivot point of Y scaling
        scaleAnimation.fillAfter = true // Needed to keep the result of the animation
        scaleAnimation.duration = 300
        imageView.startAnimation(scaleAnimation)
        captionTextView.visibility = View.VISIBLE
    }

    private fun collapseImage(imageView: ImageView, captionTextView: TextView) {
        val scaleAnimation = ScaleAnimation(
            2.0f, 1.0f,  // Start and end values for the X axis scaling
            2.0f, 1.0f,  // Start and end values for the Y axis scaling
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot point of X scaling
            Animation.RELATIVE_TO_SELF, 0.5f
        ) // Pivot point of Y scaling
        scaleAnimation.fillAfter = true // Needed to keep the result of the animation
        scaleAnimation.duration = 300
        imageView.startAnimation(scaleAnimation)
        captionTextView.visibility = View.GONE
    }

    private fun getContentUriFilePath(context: Context, contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        var filePath: String? = null
        try {
            cursor = context.contentResolver.query(contentUri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return filePath
    }
//    private fun animateZoomToLargeImage(startBounds: RectF, finalBounds: RectF, startScale: Float) {
//        binding.expandedImage.visibility = View.VISIBLE
//
//        // Set the pivot point for SCALE_X and SCALE_Y transformations to the
//        // top-left corner of the zoomed-in view. The default is the center of
//        // the view.
//        binding.expandedImage.pivotX = 0f
//        binding.expandedImage.pivotY = 0f
//
//        // Construct and run the parallel animation of the four translation and
//        // scale properties: X, Y, SCALE_X, and SCALE_Y.
//        currentAnimator = AnimatorSet().apply {
//            play(
//                ObjectAnimator.ofFloat(
//                    binding.expandedImage,
//                    View.X,
//                    startBounds.left,
//                    finalBounds.left)
//            ).apply {
//                with(ObjectAnimator.ofFloat(binding.expandedImage, View.Y, startBounds.top, finalBounds.top))
//                with(ObjectAnimator.ofFloat(binding.expandedImage, View.SCALE_X, startScale, 1f))
//                with(ObjectAnimator.ofFloat(binding.expandedImage, View.SCALE_Y, startScale, 1f))
//            }
//            duration = shortAnimationDuration.toLong()
//            interpolator = DecelerateInterpolator()
//            addListener(object : AnimatorListenerAdapter() {
//
//                override fun onAnimationEnd(animation: Animator) {
//                    currentAnimator = null
//                }
//
//                override fun onAnimationCancel(animation: Animator) {
//                    currentAnimator = null
//                }
//            })
//            start()
//        }
//    }
//
//    private fun setDismissLargeImageAnimation(thumbView: View, startBounds: RectF, startScale: Float) {
//        // When the zoomed-in image is tapped, it zooms down to the original
//        // bounds and shows the thumbnail instead of the expanded image.
//        binding.expandedImage.setOnClickListener {
//            currentAnimator?.cancel()
//
//            // Animate the four positioning and sizing properties in parallel,
//            // back to their original values.
//            currentAnimator = AnimatorSet().apply {
//                play(ObjectAnimator.ofFloat(binding.expandedImage, View.X, startBounds.left)).apply {
//                    with(ObjectAnimator.ofFloat(binding.expandedImage, View.Y, startBounds.top))
//                    with(ObjectAnimator.ofFloat(binding.expandedImage, View.SCALE_X, startScale))
//                    with(ObjectAnimator.ofFloat(binding.expandedImage, View.SCALE_Y, startScale))
//                }
//                duration = shortAnimationDuration.toLong()
//                interpolator = DecelerateInterpolator()
//                addListener(object : AnimatorListenerAdapter() {
//
//                    override fun onAnimationEnd(animation: Animator) {
//                        thumbView.alpha = 1f
//                        binding.expandedImage.visibility = View.GONE
//                        currentAnimator = null
//                    }
//
//                    override fun onAnimationCancel(animation: Animator) {
//                        thumbView.alpha = 1f
//                        binding.expandedImage.visibility = View.GONE
//                        currentAnimator = null
//                    }
//                })
//                start()
//            }
//        }
//    }
//    private fun zoomImageFromThumb(thumbView: View) {
//        // If there's an animation in progress, cancel it immediately and
//        // proceed with this one.
//        currentAnimator?.cancel()
//
//        // Calculate the starting and ending bounds for the zoomed-in image.
//        val startBoundsInt = Rect()
//        val finalBoundsInt = Rect()
//        val globalOffset = Point()
//
//        // The start bounds are the global visible rectangle of the thumbnail,
//        // and the final bounds are the global visible rectangle of the
//        // container view. Set the container view's offset as the origin for the
//        // bounds, since that's the origin for the positioning animation
//        // properties (X, Y).
//        thumbView.getGlobalVisibleRect(startBoundsInt)
//        var container = findViewById<FrameLayout>()
//        startBoundsInt.offset(-globalOffset.x, -globalOffset.y)
//        finalBoundsInt.offset(-globalOffset.x, -globalOffset.y)
//
//        val startBounds = RectF(startBoundsInt)
//        val finalBounds = RectF(finalBoundsInt)
//
//        // Using the "center crop" technique, adjust the start bounds to be the
//        // same aspect ratio as the final bounds. This prevents unwanted
//        // stretching during the animation. Calculate the start scaling factor.
//        // The end scaling factor is always 1.0.
//        val startScale: Float
//        if ((finalBounds.width() / finalBounds.height() > startBounds.width() / startBounds.height())) {
//            // Extend start bounds horizontally.
//            startScale = startBounds.height() / finalBounds.height()
//            val startWidth: Float = startScale * finalBounds.width()
//            val deltaWidth: Float = (startWidth - startBounds.width()) / 2
//            startBounds.left -= deltaWidth.toInt()
//            startBounds.right += deltaWidth.toInt()
//        } else {
//            // Extend start bounds vertically.
//            startScale = startBounds.width() / finalBounds.width()
//            val startHeight: Float = startScale * finalBounds.height()
//            val deltaHeight: Float = (startHeight - startBounds.height()) / 2f
//            startBounds.top -= deltaHeight.toInt()
//            startBounds.bottom += deltaHeight.toInt()
//        }
//
//        // Hide the thumbnail and show the zoomed-in view. When the animation
//        // begins, it positions the zoomed-in view in the place of the
//        // thumbnail.
//        thumbView.alpha = 0f
//
//        animateZoomToLargeImage(startBounds, finalBounds, startScale)
//
//        setDismissLargeImageAnimation(thumbView, startBounds, startScale)
//    }
////
//
}

