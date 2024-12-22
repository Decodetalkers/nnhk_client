package com.stein.nnhknews.nhk

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stein.nnhknews.common.Resource
import com.stein.nnhknews.data.NewsDao
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Response

private val client = OkHttpClient()

class NhkHtmlModel : ViewModel() {
    val htmlData = mutableStateOf<NhkNews?>(null)

    fun setData(data: NhkNews) {
        htmlData.value = data
    }
    val html: String
        get() = htmlData.value?.newsHtml() ?: ""
    val title: String
        get() = htmlData.value?.title ?: ""
    val audioUrl: String?
        get() = htmlData.value?.m3u8Url
}

class NhKViewModel(val newsDataBase: NewsDao) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    val state = mutableStateOf<Resource<List<NhkNews>>>(Resource.Begin)
    val cachedValues: StateFlow<List<NhkNews>> =
            newsDataBase
                    .getAllNews()
                    .stateIn(
                            viewModelScope,
                            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                            listOf()
                    )
    var timeRange: String = ""
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    init {
        val now = LocalDateTime.now(ZoneOffset.UTC)

        // Calculate yesterday and 7 days ago
        val sevendayago = now.minusDays(7)

        syncNews(now, sevendayago)
    }

    private fun genTimeRange(start: LocalDateTime, end: LocalDateTime) {
        val startDate = start.format(formatter)
        val endDate = end.format(formatter)

        timeRange = "$endDate ~ $startDate"
    }

    fun clearState() {
        val thestate by state
        if (thestate is Resource.Loading) return
        state.value = Resource.Begin
    }

    fun syncNews(start: LocalDateTime, end: LocalDateTime) {
        genTimeRange(start, end)
        val thestate by state
        if (thestate is Resource.Loading) return
        if (thestate is Resource.Success) return
        viewModelScope.launch {
            syncNewsInner(start, end).collect { response -> state.value = response }
        }
    }

    private fun syncNewsInner(start: LocalDateTime, end: LocalDateTime) = flow {
        emit(Resource.Loading)

        val request = requestNews(start, end)
        client.newCall(request)
                .enqueue(
                        object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                state.value = Resource.Failure("Unexpected code $e")
                            }

                            override fun onResponse(call: Call, response: Response) {

                                if (response.isSuccessful) {
                                    val bodys = response.body!!.string()

                                    val objs = Json.decodeFromString<List<NhkNews>>(bodys)
                                    state.value = Resource.Success(objs)
                                    newsDataBase.clearAllData()
                                    for (news in objs) {
                                        newsDataBase.insertNews(news)
                                    }
                                } else {

                                    state.value =
                                            Resource.Failure("request failed ${response.body}")
                                }
                            }
                        }
                )
    }
}
