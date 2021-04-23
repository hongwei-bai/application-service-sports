package com.hongwei.curl

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.nio.charset.Charset

object CUrlWrapper {
    fun curl(url: String): Document? {
        val curl = CUrl(url)
        val html = curl.exec(htmlResolver, null)
        return if (200 == curl.httpCode) {
            html
        } else {
            println("CUrlWrapper/curl.exec(url:" + url + ") HTTP response code: " + curl.httpCode)
            null
        }
    }

    private val htmlResolver =
        CUrl.Resolver { httpCode: Int, responseBody: ByteArray? ->
            val html = String(responseBody!!, Charset.forName("UTF-8"))
            Jsoup.parse(html)
        }
}