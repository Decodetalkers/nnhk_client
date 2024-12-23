package com.stein.nnhknews.nhk

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import okhttp3.*

val nhkFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

fun requestNews(begin: LocalDateTime, end: LocalDateTime): Request {
    val yesterdayIso = begin.atOffset(ZoneOffset.UTC).format(nhkFormatter)
    val sevenDaysAgoIso = end.atOffset(ZoneOffset.UTC).format(nhkFormatter)
    val url =
            HttpUrl.Builder()
                    .scheme("https") // Scheme (https)
                    .host("nhk.dekiru.app") // Host
                    .addPathSegment("news") // Path segment
                    .addQueryParameter("startDate", sevenDaysAgoIso) // Query parameter
                    .addQueryParameter("endDate", yesterdayIso) // Query parameter
                    .build()

    println(url)
    return Request.Builder().url(url).build()
}
