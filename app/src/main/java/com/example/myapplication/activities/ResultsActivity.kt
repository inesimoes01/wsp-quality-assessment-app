package com.example.myapplication.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
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



class ResultsActivity: AppCompatActivity() {

    private var isImageExpanded = false
    private var imageFinalBitmap: Bitmap? = null
    val dropletValues = ArrayList<BarEntry>()


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
        sendRequest(
            encodedImage,
            Settings.getReal_width(),
            Settings.getReal_height(),
            Settings.isIsAI()
        )

        setContentView(R.layout.activity_results)
        dataListing()
        setChart()

        val imageOriginalView = findViewById<ImageButton>(R.id.original_image)

        val fileNameView = findViewById<TextView>(R.id.filename)
        var fileName = "filename"
        if (filePath != null) {
            fileName = filePath.substringAfterLast('/')
        }
        fileNameView.setText(fileName)


        val backButton = findViewById<ImageButton>(R.id.imageButton)
        backButton.setOnClickListener{
            goBackButton()
        }

    }

    private fun goBackButton(){
        val acceptPhotoView = Intent(this, ResultsActivity::class.java)
        startActivity(acceptPhotoView)
    }

    private fun sendRequest(encodedImage: String?, width: Double, height: Double, isAI: Boolean) {
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
            override fun onFailure(call: Call, e: IOException) {
                Log.d("rest api", e.toString())
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
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

    private fun handleJSONResponse(responseData: String) {
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
        findViewById<TextView>(R.id.overlapped_percentage_value).text =
            overlappedPercentage.toString()

        // create graph with the value of the radius
        val radiusArray = jsonObject.getJSONArray("values_of_radius")
        val radiusValues = mutableListOf<Double>()
        for (i in 0 until radiusArray.length()) {
            radiusValues.add(radiusArray.getDouble(i))
        }
        //TODO create a graph for radius


    }



    private fun dataListing(){
        dropletValues.add(BarEntry(0.toFloat(), 13.toFloat()))
        dropletValues.add(BarEntry(1.toFloat(), 2.toFloat()))
        dropletValues.add(BarEntry(2.toFloat(), 6.toFloat()))
        dropletValues.add(BarEntry(3.toFloat(), 7.toFloat()))
        dropletValues.add(BarEntry(4.toFloat(), 10.toFloat()))

    }

    private fun setChart(){
        val tvX = findViewById<TextView>(R.id.tvXMax)
        val tvY = findViewById<TextView>(R.id.tvYMax)
        val chart = findViewById<BarChart>(R.id.chart1)

        chart.description.isEnabled = false
        chart.setMaxVisibleValueCount(50)
        chart.setDrawBarShadow(false)
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayListOf("Sales", "Profit"))
        chart.axisLeft.setDrawGridLines(false)
        chart.legend.isEnabled = false

        val barDataSetter: BarDataSet

        if (chart.data != null && chart.data.dataSetCount > 0){
            barDataSetter = chart.data.getDataSetByIndex(0) as BarDataSet
            barDataSetter.values = dropletValues
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            barDataSetter = BarDataSet(dropletValues, "DATA Set")


            barDataSetter.setColors(Color.rgb(53, 81, 62),
                Color.rgb(70, 106, 67),
                Color.rgb(244, 241, 231),
                Color.rgb(164, 176, 125),
                Color.rgb(211, 166, 66),
                Color.rgb(195, 184, 151),
            )
            barDataSetter.setDrawValues(false)

            val dataSet = ArrayList<IBarDataSet>()
            dataSet.add(barDataSetter)

            val data = BarData(dataSet)
            chart.data = data
            chart.setFitBars(true)
        }
        chart.invalidate()

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