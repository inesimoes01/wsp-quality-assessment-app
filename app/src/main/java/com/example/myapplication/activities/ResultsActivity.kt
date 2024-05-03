package com.example.myapplication.activities

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.myapplication.R


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
            val pattern = "\\(([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+), ([0-9]*\\.[0-9]+|[0-9]+)\\)".toRegex()
            val matchResult = pattern.find(stringValue)
            val values = matchResult?.destructured?.toList()

            val noDroplets = values?.get(0)?.toInt()
            val coveragePercentage = values?.get(1)?.toDouble()
            val rsfValue = values?.get(2)?.toDouble()
            val vmdValue = values?.get(3)?.toDouble()
            Log.d("PythonReader", noDroplets.toString())

            if (values != null) {
                findViewById<TextView>(R.id.coverage_percentage_value).text = coveragePercentage.toString()
                findViewById<TextView>(R.id.rsf_value).text = rsfValue.toString()
                findViewById<TextView>(R.id.vmd_value).text = vmdValue.toString()
                findViewById<TextView>(R.id.nodroplets_value).text = noDroplets.toString()
            }
//            val undistorted_image = module.callAttr("get_undistorted_image").toJava(ByteArray::class.java)
//            val bitmap1 = BitmapFactory.decodeByteArray(undistorted_image, 0, undistorted_image.size)
//            findViewById<ImageView>(R.id.undistorted_image).setImageBitmap(bitmap1)
//
//            val segmented_image = module.callAttr("get_segmented_image").toJava(ByteArray::class.java)
//            val bitmap2 = BitmapFactory.decodeByteArray(segmented_image, 0, segmented_image.size)
//            findViewById<ImageView>(R.id.segmented_image).setImageBitmap(bitmap2)
//            Log.d("PythonReader", "images?")




//            val reader = BufferedReader(InputStreamReader(process.getInputStream()))
//            val output = reader.readLine()
//            val values = output.substring(1, output.length - 1).split(", ".toRegex())
//                .dropLastWhile { it.isEmpty() }
//                .toTypedArray()
//            val result = Arrays.asList(*values)

            //findViewById<TextView>(R.id.coverage_percentage_value).text = bytes.toString()

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



