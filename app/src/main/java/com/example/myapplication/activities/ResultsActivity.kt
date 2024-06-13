package com.example.myapplication.activities

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.myapplication.R
import java.util.regex.Pattern


class ResultsActivity: AppCompatActivity() {

    lateinit var result: PyObject

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
//            val pattern = "\\(([0-9]+), ([0-9]*\\.[0-9]+), ([0-9]*\\.[0-9]+), ([0-9]*\\.[0-9]+), '([A-Za-z0-9+/=]+)', '([A-Za-z0-9+/=]+)'\\)".toRegex()
//            //val pattern = "\\(([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+)\\)".toRegex()
//            val matchResult = pattern.find(stringValue)
//            val values = matchResult?.destructured?.toList()
//
//            val noDroplets = values?.get(0)?.toInt()
//            val coveragePercentage = values?.get(1)?.toDouble()
//            val rsfValue = values?.get(2)?.toDouble()
//            val vmdValue = values?.get(3)?.toDouble()
//            val image_original = values?.get(4)
//            val image_gray = values?.get(5)
//            Log.d("PythonReader", noDroplets.toString())

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

                findViewById<ImageView>(R.id.original_image).setImageBitmap(originalBitmap)
                findViewById<ImageView>(R.id.undistorted_image).setImageBitmap(grayBitmap)
                findViewById<ImageView>(R.id.segmented_image).setImageBitmap(grayBitmap)

            }


        } catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    fun getContentUriFilePath(context: Context, contentUri: Uri): String? {
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



