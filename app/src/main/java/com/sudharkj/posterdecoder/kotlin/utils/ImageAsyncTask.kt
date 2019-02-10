package com.sudharkj.posterdecoder.kotlin.utils

import android.net.Uri
import android.os.AsyncTask
import com.sudharkj.posterdecoder.kotlin.models.AsyncObject
import com.sudharkj.posterdecoder.kotlin.models.AsyncResponse

class ImageAsyncTask(view: AsyncObject<Uri?>, response: AsyncResponse) : AsyncTask<Void, Int, Uri?>() {
    private val view: AsyncObject<Uri?> = view
    private val response: AsyncResponse = response

    override fun doInBackground(vararg params: Void?): Uri? {
        return view.process()
    }

    override fun onPostExecute(result: Uri?) {
        response.processFinish(result)
    }
}
