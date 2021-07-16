package com.hongwei.util

import java.text.SimpleDateFormat
import java.util.*

object EspnDateTimeParseUtil {
    //2021-04-07T02:00Z
    fun parseDate(dateString: String): Date? = try {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        simpleDateFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }
}