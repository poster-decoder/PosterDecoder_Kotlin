package com.sudharkj.posterdecoder.kotlin.models

import android.net.Uri

interface AsyncResponse {
    fun processFinish(uri: Uri?)
}