package com.example.lifecycle_management_and_intents

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever.BitmapParams
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var firstName: EditText
    lateinit var middleName: EditText
    lateinit var lastName: EditText
    lateinit var fullTextView: TextView

    private var mThumbnail: Bitmap? = null
    private var filePathString: String? = null

    private lateinit var imageBtn: ImageButton
    private lateinit var saveBtn: Button

    private var signedIn : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firstName = findViewById(R.id.firstNameTxt)
        middleName = findViewById(R.id.midNameTxt)
        lastName = findViewById(R.id.lastNameTxt)

        fullTextView = findViewById(R.id.fullTextView)

        imageBtn = findViewById(R.id.imageButton)
        saveBtn = findViewById(R.id.saveBtn)

        imageBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)

        if(savedInstanceState != null){
            fullTextView!!.setText("${savedInstanceState.getString("FN_TEXT")} ${savedInstanceState.getString("MN_TEXT")} ${savedInstanceState.getString("LN_TEXT")}")

            filePathString = savedInstanceState.getString("THUMB_PATH")
            mThumbnail = BitmapFactory.decodeFile(filePathString)
            if(mThumbnail != null){
                imageBtn!!.setImageBitmap(mThumbnail)
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)


        //put them in the outgoing bundle
        outState.putString("FN_TEXT", firstName!!.text.toString())
        outState.putString("MN_TEXT", middleName!!.text.toString())
        outState.putString("LN_TEXT", lastName!!.text.toString())
        outState.putString("THUMB_PATH", filePathString)
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.imageButton -> {
                var cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try{
                    cameraActivity.launch(cameraIntent)
                }catch (ex:ActivityNotFoundException){
                    //TODO
                }
            }

            R.id.saveBtn ->{
                signedIn = Intent(this, SignedIn::class.java)
                try{
                    signedIn!!.putExtra("FN_TEXT", firstName!!.text.toString())
                    signedIn!!.putExtra("LN_TEXT", lastName!!.text.toString())

                    startActivity(signedIn)
                }catch (ex: ActivityNotFoundException){

                }
            }
        }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if(result.resultCode == RESULT_OK){
            val extras = result.data!!.extras
            mThumbnail = extras!!["data"] as Bitmap?

            //open file and write to it
            if(isExternalStorageWritable){
                filePathString = saveImage(mThumbnail)
                imageBtn!!.setImageBitmap(mThumbnail)
            }else{
                Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
}
