package it.unipi.di.sam.moveit.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import it.unipi.di.sam.moveit.utils.Constants.TEMA_SYSTEM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension porpriety (Context.dataStore) e property delegateion :
// aggiungiamo una proprietà a Context e delegiamo la funzione a ciò che ci trona preferencesDataStore
// ossia un oggeto che crea un Datastore e poi ci fa usare sempre quell'istanza anche successivamente

val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreferences {


    private val KEY_TEMA = stringPreferencesKey("tema")

    // emette il tema salvato ogni volta che cambia. Se non è mai
    // stato salvato nulla si usa TEMA_SYSTEM come default.
    fun themeFlow(context: Context): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[KEY_TEMA] ?: TEMA_SYSTEM
        }

    suspend fun setTheme(context: Context, theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TEMA] = theme
        }
    }
}