package com.example.collegeschedule.data.network

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "favorites_store")

class FavoritesDataStore(private val context: Context) {

    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_groups")

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun toggle(groupName: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[FAVORITES_KEY] ?: emptySet()
            prefs[FAVORITES_KEY] =
                if (current.contains(groupName)) current - groupName
                else current + groupName
        }
    }
}
