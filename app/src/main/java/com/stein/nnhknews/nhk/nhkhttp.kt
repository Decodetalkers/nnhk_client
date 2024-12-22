package com.stein.nnhknews.nhk

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import okhttp3.*

val isoFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun requestNews(begin: LocalDateTime, end: LocalDateTime): Request {

    val yesterdayIso = begin.atOffset(ZoneOffset.UTC).format(isoFormatter)
    val sevenDaysAgoIso = end.atOffset(ZoneOffset.UTC).format(isoFormatter)
    val url =
            HttpUrl.Builder()
                    .scheme("https") // Scheme (https)
                    .host("nhk.dekiru.app") // Host
                    .addPathSegment("news") // Path segment
                    .addQueryParameter("startDate", sevenDaysAgoIso) // Query parameter
                    .addQueryParameter("endDate", yesterdayIso) // Query parameter
                    .build()

    return Request.Builder().url(url).build()
}
