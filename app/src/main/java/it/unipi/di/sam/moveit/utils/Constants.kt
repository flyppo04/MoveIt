package it.unipi.di.sam.moveit.utils

object Constants{
    // Sensori
    const val ALPHA_FILTRO_PASSA_BASSO = 0.8f
    const val MAX_INTENSITA_ACC = 18.0f
    const val MAX_INTENSITA_GYRO = 15.0f
    const val PESO_ACC = 0.6f
    const val PESO_GYRO = 0.4f
    const val SOGLIA_MOVIMENTO = 0.05f
    const val MAX_DISTANZA_VETTORIALE = 10.0f /*TODO*/

    // Gioco
    const val INTERVALLO_CAMPIONAMENTO_MS = 300L
    const val DURATA_FEEDBACK_MS = 1200L
    const val TOLLERANZA_TIMESTAMP_MS = 300L

    // Tema
    const val TEMA_LIGHT = "light"
    const val TEMA_DARK = "dark"
    const val TEMA_SYSTEM = "system"
    const val KEY_TEMA = "tema"
}