package com.stein.nnhknews

import android.app.Application
import com.stein.nnhknews.data.AppDataContainer

class InventoryApplication : Application() {

    /** AppContainer instance used by the rest of classes to obtain dependencies */
    companion object {
        lateinit var container: AppDataContainer
    }

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
