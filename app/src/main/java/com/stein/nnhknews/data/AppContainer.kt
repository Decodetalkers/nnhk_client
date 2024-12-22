package com.stein.nnhknews.data

import android.content.Context

class AppDataContainer(private val context: Context) {
    val newRepository: NewsDao by lazy { NewsDatabase.getDatabase(context).newsDao() }
}
