    package it.unipi.di.sam.moveit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// ─────────────────────────────────────────────────────────────────────────────
// SCHEMI COLORE
// ─────────────────────────────────────────────────────────────────────────────
//
// Un ColorScheme Material 3 è un set di slot con nomi semantici.
// I principali:
//
//   primary         → colore principale (pulsanti, elementi attivi)
//   onPrimary       → testo/icone SOPRA primary (deve avere contrasto sufficiente)
//   primaryContainer → versione "pastello" di primary (sfondo di chip, badge)
//   onPrimaryContainer → testo sopra primaryContainer
//
//   secondary / onSecondary → colore di supporto
//   tertiary / onTertiary   → colore di accento terziario
//
//   background / onBackground → sfondo app e testo sopra
//   surface / onSurface       → sfondo di card, dialog e testo sopra
//   surfaceVariant            → variante di surface per elementi come chip
//
//   error / onError           → colori per stati di errore
//
// PERCHÉ slot semantici e non "red, blue, green"?
// Material 3 garantisce che i componenti (Button, Card, TopAppBar) usino
// automaticamente gli slot giusti. Un Button usa sempre "primary" come sfondo
// e "onPrimary" per il testo — senza che tu debba specificarlo ogni volta.
// Cambi i colori in un posto solo e tutta l'app si aggiorna.
//
// ─────────────────────────────────────────────────────────────────────────────

    // Schema per il TEMA CHIARO
    private val LightColorScheme = lightColorScheme(
        primary          = MoveItOrange,
        onPrimary        = MoveItOnPrimary,          // bianco sul arancione → contrasto OK
        primaryContainer = MoveItYellow,             // giallo come container (es. chip selezionato)
        onPrimaryContainer = MoveItOnBackgroundLight,

        secondary        = MoveItGreen,
        onSecondary      = MoveItOnPrimary,

        tertiary         = MoveItYellow,
        onTertiary       = MoveItOnBackgroundLight,

        background       = MoveItBackgroundLight,
        onBackground     = MoveItOnBackgroundLight,

        surface          = MoveItSurfaceLight,
        onSurface        = MoveItOnBackgroundLight,

        error            = MoveItRed,
        onError          = MoveItOnPrimary
    )

    // Schema per il TEMA SCURO
    private val DarkColorScheme = darkColorScheme(
        primary          = MoveItOrange,
        onPrimary        = MoveItOnPrimary,
        primaryContainer = MoveItOrangeDark,
        onPrimaryContainer = MoveItOnBackgroundDark,

        secondary        = MoveItGreen,
        onSecondary      = MoveItOnBackgroundDark,

        tertiary         = MoveItYellow,
        onTertiary       = MoveItOnBackgroundDark,

        background       = MoveItBackgroundDark,
        onBackground     = MoveItOnBackgroundDark,

        surface          = MoveItSurfaceDark,
        onSurface        = MoveItOnBackgroundDark,

        error            = MoveItRed,
        onError          = MoveItOnPrimary
    )

    // ─────────────────────────────────────────────────────────────────────────────
// COMPOSABLE TEMA — MoveItTheme
// ─────────────────────────────────────────────────────────────────────────────
//
// Questo è il composable "radice" che avvolge tutta l'app in MainActivity.
//
// Parametri:
//   darkTheme: Boolean — se true applica DarkColorScheme, altrimenti LightColorScheme.
//              Viene controllato da SettingsViewModel e passa attraverso MainActivity.
//
//   content: @Composable () -> Unit — questo parametro è una "lambda composable",
//              ovvero il contenuto che viene renderizzato DENTRO il tema.
//              Il tipo () -> Unit significa "funzione senza parametri che non ritorna nulla".
//              @Composable davanti indica che è una funzione composable (può contenere UI).
//
// PATTERN: "content slot"
//   Passare il contenuto come lambda è il pattern standard in Compose per i
//   composable "contenitore". Permette di scrivere:
//
//     MoveItTheme(darkTheme = true) {
//         HomeScreen()   // ← questo va nel "content"
//     }
//
//   La parentesi graffa dopo la chiamata è la "trailing lambda" di Kotlin:
//   quando l'ultimo parametro è una funzione, si può scrivere fuori dalle parentesi.
//
// ─────────────────────────────────────────────────────────────────────────────
    @Composable
    fun MoveItTheme(
        darkTheme: Boolean = false,          // default: tema chiaro
        content: @Composable () -> Unit      // il contenuto dell'app
    ) {
        // Seleziona lo schema colori in base al parametro darkTheme.
        // Questo è un semplice if-expression: in Kotlin, if è un'espressione
        // (restituisce un valore) oltre che uno statement. Molto più conciso di Java.
        val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

        // MaterialTheme "inietta" il colorScheme e la typography nell'albero
        // dei composable tramite la CompositionLocal di Compose.
        // Tutti i composable figli possono accedervi con:
        //   MaterialTheme.colorScheme.primary
        //   MaterialTheme.typography.bodyLarge
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content    // il contenuto dell'app viene renderizzato qui
        )
    }
