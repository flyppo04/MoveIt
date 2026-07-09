package it.unipi.di.sam.moveit.ui.theme

import androidx.compose.ui.graphics.Color

//Fatto con AI

// Color è la classe di Compose che rappresenta un colore.
// Vive nel package androidx.compose.ui.graphics, separato da android.graphics.Color
// (quello "vecchio" del sistema View). Non mischiarli mai.
//
// ─────────────────────────────────────────────────────────────────────────────
// PALETTE COLORI — MoveIt
// ─────────────────────────────────────────────────────────────────────────────
//
// Questo file definisce SOLO i valori crudi dei colori (come costanti).
// NON decide dove vengono usati — quello è compito di Theme.kt.
//
// Notazione: Color(0xFFRRGGBB)
//   - 0x   → prefisso per il numero esadecimale in Kotlin
//   - FF   → canale Alpha (opacità): FF = 255 = completamente opaco
//   - RR   → canale Rosso (Red)
//   - GG   → canale Verde (Green)
//   - BB   → canale Blu (Blue)
//
// Perché "val" e non "const val"?
//   Color è un oggetto (value class), non un tipo primitivo come Int o String.
//   "const val" in Kotlin funziona solo con primitivi e String — quindi qui usiamo "val".
//
// ─────────────────────────────────────────────────────────────────────────────

// ── COLORI PRIMARI ───────────────────────────────────────────────────────────
// Arancione energico — il colore "firma" dell'app.
// Usato per pulsanti principali, TopAppBar, elementi in evidenza.
val MoveItOrange = Color(0xFFFF6B35)

// Variante più scura dell'arancione, per stati pressed/hover o containers.
val MoveItOrangeDark = Color(0xFFCC4A1A)

// ── ACCENTI ──────────────────────────────────────────────────────────────────
// Giallo brillante — per elementi secondari, highlights, stelle.
val MoveItYellow = Color(0xFFFFD60A)

// Verde menta — feedback positivo ("PERFECT!"), conferme.
val MoveItGreen = Color(0xFF06D6A0)

// Rosa-rosso — feedback negativo ("MISS"), errori.
val MoveItRed = Color(0xFFEF476F)

// ── LIGHT THEME ──────────────────────────────────────────────────────────────
// Background: lo sfondo principale delle schermate nel tema chiaro.
// Bianco caldo (leggermente panna) per non essere troppo freddo.
val MoveItBackgroundLight = Color(0xFFEDE3CE)

// Surface: il colore delle card, bottom sheet, dialog nel tema chiaro.
val MoveItSurfaceLight = Color(0xFFF5E8D0)

// On-colors: il colore del testo/icone SOPRA un colore di sfondo.
// Su Primary (arancione) il testo deve essere bianco per contrasto sufficiente.
val MoveItOnPrimary = Color(0xFFFFFFFF)

// Su Background chiaro, testo scuro.
val MoveItOnBackgroundLight = Color(0xFF3E2E1E)

// ── DARK THEME ───────────────────────────────────────────────────────────────
// Background scuro: bruno molto scuro, non nero puro (meno affaticante per gli occhi).
val MoveItBackgroundDark = Color(0xFF2B2419)   // bruno-carbone morbido, non nero

// Surface scura: leggermente più chiara del background per creare profondità.
val MoveItSurfaceDark = Color(0xFF3A311F)

// Su Background scuro, testo chiaro.
val MoveItOnBackgroundDark = Color(0xFFEFE3CF)


/*
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
*/