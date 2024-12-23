package com.stein.nnhknews

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.stein.nnhknews.data.AppDataContainer
import com.stein.nnhknews.data.UserPreferencesRepository

private const val LAYOUT_PREFERENCE_NAME = "layout_preferences"
private val Context.dataStore: DataStore<Preferences> by
        preferencesDataStore(name = LAYOUT_PREFERENCE_NAME)

class InventoryApplication : Application() {

    /** AppContainer instance used by the rest of classes to obtain dependencies */
    lateinit var userPreferencesRepository: UserPreferencesRepository

    lateinit var container: AppDataContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        userPreferencesRepository = UserPreferencesRepository(dataStore)
    }
}
