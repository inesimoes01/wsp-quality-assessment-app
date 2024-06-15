package com.example.myapplication.activities


import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.myapplication.R
import java.util.regex.Pattern


class ResultsActivity: AppCompatActivity() {


    private var isImageExpanded = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // get image uri sent from the camera view activity
        val imageUri = intent.getStringExtra("uri")
        val contentUri = Uri.parse(imageUri)
        val filePath = getContentUriFilePath(this, contentUri)


        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("main")

        if(!Python.isStarted())
            Python.start(AndroidPlatform(this))


        try {
            //Log.d("PythonReader", filePath.toString())
            val result = module.callAttr("main", filePath, 76, 26)//.toJava(ByteArray::class.java)
            val stringValue: String = result.toString()

            //val stringFromBytes: String = result.toString(Charsets.UTF_8)
            Log.d("PythonReader", stringValue)
            val dropletImage = findViewById<ImageView>(R.id.original_image)

            val captionImageView = findViewById<TextView>(R.id.captionTextView)


            val pattern = Pattern.compile("\\(([0-9]+), ([0-9]+), ([0-9]+), ([0-9]+), '([A-Za-z0-9+/=]+)', '([A-Za-z0-9+/=]+)'")
            val matcher = pattern.matcher(stringValue)

            if (matcher.find()) {
                val noDroplets = matcher.group(1)?.toInt()
                val coveragePercentage = matcher.group(2)?.toDouble()
                val rsfValue = matcher.group(3)?.toDouble()
                val vmdValue = matcher.group(4)?.toDouble()
                val image_original = matcher.group(5)
                val image_gray = matcher.group(6)

                Log.d("PythonReader", noDroplets.toString())
                findViewById<TextView>(R.id.coverage_percentage_value).text = coveragePercentage.toString()
                findViewById<TextView>(R.id.rsf_value).text = rsfValue.toString()
                findViewById<TextView>(R.id.vmd_value).text = vmdValue.toString()
                findViewById<TextView>(R.id.nodroplets_value).text = noDroplets.toString()

                val imageBytes = Base64.decode(image_original, Base64.DEFAULT)
                val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

                val grayImageBytes = Base64.decode(image_gray, Base64.DEFAULT)
                val grayBitmap = BitmapFactory.decodeByteArray(grayImageBytes, 0, grayImageBytes.size)

                dropletImage.setImageBitmap(originalBitmap)
            }

//            dropletImage.setOnClickListener(View.OnClickListener {
//                if (isImageExpanded) {
//                    collapseImage(dropletImage, captionImageView)
//                } else {
//                    expandImage(dropletImage, captionImageView)
//                }
//                isImageExpanded = !isImageExpanded
//            })


        } catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
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


}



