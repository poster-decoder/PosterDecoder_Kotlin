package com.sudharkj.posterdecoder.kotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.sudharkj.posterdecoder.kotlin.models.AsyncObject
import com.sudharkj.posterdecoder.kotlin.models.AsyncResponse
import com.sudharkj.posterdecoder.kotlin.utils.Helper
import com.sudharkj.posterdecoder.kotlin.utils.ImageAsyncTask
import com.sudharkj.posterdecoder.kotlin.views.DrawingView

import kotlinx.android.synthetic.main.activity_boundary.*
import kotlinx.android.synthetic.main.content_boundary.*
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class BoundaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boundary)
        setSupportActionBar(toolbar)

        val photoUri: Uri = intent.extras.get(MediaStore.EXTRA_OUTPUT) as Uri
        boundary_image.setImageURI(photoUri)

        boundary_next.setOnClickListener { view ->
            ImageAsyncTask(CroppedImage(boundary_image), CroppedImageUri(this)).execute()
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private class CroppedImage(view: DrawingView) : AsyncObject<Uri?> {
        val view: DrawingView = view
        companion object {
            const val TAG: String = "CroppedImage"
        }

        fun getCroppedBitmap(): Bitmap {
            val bitmap = view.drawable.toBitmap()

            val bRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val cRatio = view.width.toFloat() / view.height.toFloat()
            val ratio =
                if (bRatio < cRatio) {
                    view.height.toFloat() / bitmap.height.toFloat()
                } else {
                    view.width.toFloat() / bitmap.width.toFloat()
                }
            val (iWidth, iHeight) =
                floor(bitmap.width * ratio).toInt() to floor(bitmap.height * ratio).toInt()
            val bitmapRect = Rect(
                ceil((view.width - iWidth) / 2f).toInt(),
                ceil((view.height - iHeight) / 2f).toInt(),
                floor((view.width + iWidth) / 2f).toInt(),
                floor((view.height + iHeight) / 2f).toInt()
            )
            val rect = view.getCropRect()
            val cropRect = Rect(
                max(bitmapRect.left, rect.left),
                max(bitmapRect.top, rect.top),
                min(bitmapRect.right, rect.right),
                min(bitmapRect.bottom, rect.bottom)
            )

            val bitmapCropRect = Rect(
                ceil((cropRect.left - bitmapRect.left) / ratio).toInt(),
                ceil((cropRect.top - bitmapRect.top) / ratio).toInt(),
                floor((cropRect.right - bitmapRect.left) / ratio).toInt(),
                floor((cropRect.bottom - bitmapRect.top) / ratio).toInt()
            )

            return Bitmap.createBitmap(bitmap, bitmapCropRect.left, bitmapCropRect.top,
                bitmapCropRect.right - bitmapCropRect.left, bitmapCropRect.bottom - bitmapCropRect.top)
        }

        fun saveBitmap(croppedBitmap: Bitmap): Uri? {
            try {
                val file = Helper().getFile(view.context, R.string.cropped_image_prefix)
                FileOutputStream(file).use { fileStream: FileOutputStream ->
                    croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileStream)
                }
                Log.d(TAG, "Saved cropped bitmap to ${file.absolutePath}")
                return file.toUri()
            } catch (ex: IOException) {
                Log.e(TAG, view.context.getString(R.string.error_image_creation), ex)
            }
            return null
        }

        override fun process(): Uri? {
            val croppedBitmap = getCroppedBitmap()
            Log.d(TAG, "Obtained cropped bitmap")
            return saveBitmap(croppedBitmap)
        }
    }

    private class CroppedImageUri(activity: Activity) : AsyncResponse {
        val activity: Activity = activity
        companion object {
            const val TAG: String = "CroppedImageUri"
        }

        override fun processFinish(uri: Uri?) {
            Log.d(TAG, "Processed image crop uri")
            uri?.let {
                Intent(activity, ContourActivity::class.java).also {
                    it.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    activity.startActivity(it)
                }
            }
        }
    }
}
