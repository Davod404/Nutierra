package com.example.nutierra.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion")

object SessionPreferences {

    private val USUARIO_ID = intPreferencesKey("usuario_id")

    suspend fun guardarUsuarioId(context: Context, usuarioId: Int) {
        context.dataStore.edit { prefs ->
            prefs[USUARIO_ID] = usuarioId
        }
    }

    fun leerUsuarioId(context: Context): Flow<Int?> {
        return context.dataStore.data.map { prefs ->
            prefs[USUARIO_ID]
        }
    }

    suspend fun borrarUsuarioId(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(USUARIO_ID)
        }
    }
}