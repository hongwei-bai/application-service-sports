package com.hongwei.util

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.io.FileOutputStream

object WebImageDownloadUtil {
    fun readWebImage(urlString: String): ByteArray {
        val url = URL(urlString)
        val `in`: InputStream = BufferedInputStream(url.openStream())
        val out = ByteArrayOutputStream()
        val buf = ByteArray(1024)
        var n = 0
        while (-1 != `in`.read(buf).also { n = it }) {
            out.write(buf, 0, n)
        }
        out.close()
        `in`.close()
        val response: ByteArray = out.toByteArray()
        return response
    }

    fun downloadWebImage(urlString: String, dest: String): Boolean {
        try {
            val response = readWebImage(urlString)
            val fos = FileOutputStream(dest)
            fos.write(response)
            fos.close()
            return true
        } catch (e: Exception) {
            println("WebImageDownloadUtil::downloadWebImage caught exception: ${e.localizedMessage}")
        }
        return false
    }
}