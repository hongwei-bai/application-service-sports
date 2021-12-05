package com.hongwei.util

import java.io.File

object UnixFilePermissionUtil {
    fun chmod777(filePath: String) {
        val fileHandle = File(filePath)
        fileHandle.setReadable(true, false)
        fileHandle.setExecutable(true, false)
        fileHandle.setWritable(true, false)
    }
}