package com.example.myapplication.activities

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
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // get image uri sent from the camera view activity
        val imageUri = intent.getStringExtra("uri")
        val contentUri = Uri.parse(imageUri)
        val filePath = getContentUriFilePath(this, contentUri)

        // get settings values
        sendRequest(filePath, 2.6, 7.6, false)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun sendRequest(filepath: String?, width : Double, height : Double, isAI: Boolean) {
        val client = OkHttpClient()
        val url = "http://192.168.1.60:5000/perform_segmentation"



        val bitmap = BitmapFactory.decodeFile(filepath)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)


        val jsonObject = JSONObject().apply {
            put("image_uri", encodedImage)
            put("width", width)
            put("height", height)
            put("isAI", isAI)
        }

        val body = jsonObject.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        // create JSON object

//        val request = Request.Builder()
//            .url(url)
//            .post(jsonObject.toString().toRequestBody("application/json".toMediaType()))
//            .build()


        // send request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException){

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
                    println("Request failed: ${response.message}")
                }
            }
        })
    }

    private fun handleJSONResponse(responseData : String){
        Log.d("REST API", "YESSS")
        val jsonObject = JSONObject(responseData)

        val imageBitmap = jsonObject.getString("image_bitmap")

        val vmd = jsonObject.getDouble("vmd")
        val rsf = jsonObject.getDouble("rsf")
        val coveragePercentage = jsonObject.getDouble("coverage_percentage")
        val numberDroplets = jsonObject.getInt("number_droplets")
        val overlappedPercentage = jsonObject.getDouble("overlapped_percentage")

        val radiusArray = jsonObject.getJSONArray("values_of_radius")
        val radiusValues = mutableListOf<Double>()
        for (i in 0 until radiusArray.length()) {
            radiusValues.add(radiusArray.getDouble(i))
        }

        findViewById<TextView>(R.id.coverage_percentage_value).text = coveragePercentage.toString()
        findViewById<TextView>(R.id.rsf_value).text = rsf.toString()
        findViewById<TextView>(R.id.vmd_value).text = vmd.toString()
        findViewById<TextView>(R.id.nodroplets_value).text = numberDroplets.toString()
        findViewById<TextView>(R.id.overlapped_percentage_value).text = overlappedPercentage.toString()


        val imageBytes = Base64.decode(imageBitmap, Base64.DEFAULT)
        val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        findViewById<ImageView>(R.id.original_image).setImageBitmap(image)


        //TODO create a graph for radius
    }
//    private fun populateBarChart(radiusValues: List<Double>) {
//        val entries = radiusValues.mapIndexed { index, value ->
//            BarEntry(index.toFloat(), value.toFloat())
//        }
//        val dataSet = BarDataSet(entries, "Radius Values")
//        val barData = BarData(dataSet)
//        barChart.data = barData
//        barChart.invalidate() // Refresh the chart
//    }
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



