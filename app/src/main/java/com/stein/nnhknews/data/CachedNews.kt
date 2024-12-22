package com.stein.nnhknews.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class CachedDataRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val START_DATE = longPreferencesKey("start_date")
        val END_DATE = longPreferencesKey("end_date")
        const val TAG = "CachedDataRepository"
    }

    val startDate: Flow<Long?> =
            dataStore.data
                    .catch {
                        if (it is IOException) {
                            Log.e(TAG, "Error reading preferences.", it)
                            emit(emptyPreferences())
                        } else {
                            throw it
                        }
                    }
                    .map { preferences -> preferences[START_DATE] }
    val endDate: Flow<Long?> =
            dataStore.data
                    .catch {
                        if (it is IOException) {
                            Log.e(TAG, "Error reading preferences.", it)
                            emit(emptyPreferences())
                        } else {
                            throw it
                        }
                    }
                    .map { preferences -> preferences[END_DATE] }
}
