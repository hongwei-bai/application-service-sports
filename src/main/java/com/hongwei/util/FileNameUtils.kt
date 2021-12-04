package com.hongwei.util

object FileNameUtils {
    fun getFileName(url: String): String {
        if (url != null && url.isNotEmpty()) {
            val token = url.lastIndexOf('\\').coerceAtLeast(url.lastIndexOf('/'))
            if (token > -1 && token < url.length) {
                return url.substring(token + 1)
            }
        }
        return url
    }
}