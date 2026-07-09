package it.unipi.di.sam.moveit.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import it.unipi.di.sam.moveit.utils.Constants.TEMA_SYSTEM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Property delegate: crea UN solo DataStore per tutta l'app, chiamato
// "settings". Va dichiarata una volta sola a livello di file (non dentro
// una classe/funzione) - tutte le letture/scritture nell'app useranno
// sempre la stessa istanza.
val Context.dataStore by preferencesDataStore(name = "settings")

object ThemePreferences {


    private val KEY_TEMA = stringPreferencesKey("tema")

    // Flow che emette il tema salvato ogni volta che cambia. Se non è mai
    // stato salvato nulla (prima apertura dell'app), preferences[KEY_TEMA]
    // è null e si usa TEMA_SYSTEM come default.
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