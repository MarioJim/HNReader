package org.team4.hnreader.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStoreHelper(ctx: Context) {
    companion object {
        @Volatile
        private var instance: DataStoreHelper? = null
        fun getInstance(ctx: Context) =
            instance ?: synchronized(this) {
                instance ?: DataStoreHelper(ctx).also { instance = it }
            }

        val THEME = stringPreferencesKey("theme")
        const val LIGHT_THEME = "Light"
        const val DARK_THEME = "Dark"
    }

    val dataStore: DataStore<Preferences> = ctx.createDataStore(name = "settings")

    suspend fun saveThemeConfig(newTheme: String) {
        dataStore.edit { settings ->
            settings[THEME] = newTheme
        }
    }

    val currentTheme: Flow<String> = runBlocking {
        dataStore.data.map { preferences ->
            preferences[THEME] ?: LIGHT_THEME
        }
    }
}