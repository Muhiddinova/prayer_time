package com.example.prayertime.ui.mosque

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DownloadUrl {
private var TAG="DownloadUrl"
    @Synchronized
    fun readUrl(strUrl: String?): String {

        var mData = ""
        var mIStream: InputStream? = null
        var mUrlConnection: HttpURLConnection? = null
        try {
            val sUrl = URL(strUrl)
            mUrlConnection = sUrl.openConnection() as HttpURLConnection
            mUrlConnection.connect()
            mIStream = mUrlConnection.inputStream
            val br = BufferedReader(InputStreamReader(mIStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            mData = sb.toString()
            Log.d(TAG, "readUrl: $mData")
            br.close()
        } catch (e: Exception) {
            Log.e(TAG, "readUrl: ", e)
        } finally {
            mIStream?.close()
            mUrlConnection?.disconnect()
        }
        return mData
    }
}