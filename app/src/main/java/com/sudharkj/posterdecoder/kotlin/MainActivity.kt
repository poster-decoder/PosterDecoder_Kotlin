package com.sudharkj.posterdecoder.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.sudharkj.posterdecoder.kotlin.utils.Helper

import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab_gallery.setOnClickListener {
            dispatchLoadPictureIntent()
        }

        fab_camera.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val TAG = "MainActivity" /* Logger tag. */
        const val REQUEST_IMAGE_LOAD = 1 /* Any integer != -1 is forces for valid request code check */
        const val REQUEST_IMAGE_CAPTURE = 2 /* Any integer != -1 is forces for valid request code check */
    }

    private var imageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_IMAGE_LOAD || requestCode == REQUEST_IMAGE_CAPTURE) && resultCode == RESULT_OK) {
            data?.let {
                if (requestCode == REQUEST_IMAGE_LOAD) {
                    imageUri = data.data
                }
            }
            imageUri?.let {
                Intent(this, BoundaryActivity::class.java).also {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                    startActivity(it)
                }
            }
        }
    }

    private fun dispatchLoadPictureIntent() {
        Intent(Intent.ACTION_PICK).also { loadPictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            loadPictureIntent.type = "image/*"
            startActivityForResult(loadPictureIntent, REQUEST_IMAGE_LOAD)
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File = try {
                    Helper().getFile(applicationContext, R.string.image_prefix).apply {
                        imageUri = toUri()
                        galleryAddPic()
                    }
                } catch (ex: IOException) {
                    // Error occurred while creating the File and return null
                    Log.e(TAG, getString(R.string.error_image_creation), ex)
                    null
                }!!
                photoFile.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        getString(R.string.base_package),
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            mediaScanIntent.data = imageUri
            sendBroadcast(mediaScanIntent)
        }
    }
}
