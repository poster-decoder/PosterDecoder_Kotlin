package com.sudharkj.posterdecoder.kotlin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val photoUri: Uri = intent.extras.get(MediaStore.EXTRA_OUTPUT) as Uri
        poster_image.setImageURI(photoUri)
    }
}
