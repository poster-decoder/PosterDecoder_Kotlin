package com.sudharkj.posterdecoder.kotlin.utils

import android.content.Context
import android.os.Environment
import com.sudharkj.posterdecoder.kotlin.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Helper {

    @Throws(IOException::class)
    fun getFile(context: Context, prefixId: Int): File {
        val timeStamp: String =
                SimpleDateFormat(context.getString(R.string.date_format), Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${context.getString(prefixId)}_$timeStamp", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}