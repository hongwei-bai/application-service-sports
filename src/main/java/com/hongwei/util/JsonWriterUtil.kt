package com.hongwei.util

import com.google.gson.Gson
import java.io.FileWriter
import java.io.IOException

object JsonWriterUtil {
    fun writeJSONObject(obj: Any, file: String) {
        writeJsonString(Gson().toJson(obj), file)
    }

    fun writeJsonString(jsonString: String, file: String) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(file)
            fileWriter.write(jsonString)
        } catch (e: IOException) {
            throw e
        } finally {
            fileWriter?.flush()
            fileWriter?.close()
        }
    }
}