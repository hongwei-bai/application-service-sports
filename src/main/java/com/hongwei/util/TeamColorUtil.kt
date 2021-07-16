package com.hongwei.util

import java.math.BigInteger

object TeamColorUtil {
    fun convertColorHexStringToLong(colorHexString: String, argbString: String? = when (colorHexString.length) {
        6 -> "FF$colorHexString"
        8 -> colorHexString
        else -> null
    }): Long = argbString?.let {
        BigInteger(it, 16).toLong()
    } ?: 0L
}