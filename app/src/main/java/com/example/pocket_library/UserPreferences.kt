package com.example.pocket_library

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.room.Index
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_pref")

object PrefKeys{
    val LAST_QUERY = stringPreferencesKey("last_query")
    val LAST_SCROLL_INDEX = intPreferencesKey("last_scroll_index")
    val LAST_SCROLL_OFFSET = intPreferencesKey("last_scroll_offset")
}

suspend fun saveLastQuery(context: Context, query: String){
    context.dataStore.edit { prefs ->
        prefs[PrefKeys.LAST_QUERY] = query
    }
}

fun getLastQuery(context: Context) = context.dataStore.data.map { prefs ->
    prefs[PrefKeys.LAST_QUERY] ?: ""
}

suspend fun saveScrollPosition(context: Context, index: Int, offset: Int){
    context.dataStore.edit { prefs ->
    prefs[PrefKeys.LAST_SCROLL_INDEX] = index
    prefs[PrefKeys.LAST_SCROLL_OFFSET] = offset
    }
}

fun getScrollPosition(context: Context) = context.dataStore.data.map { prefs->
    Pair(
        prefs[PrefKeys.LAST_SCROLL_INDEX] ?: 0,
        prefs[PrefKeys.LAST_SCROLL_OFFSET] ?: 0
    )
}