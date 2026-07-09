package it.unipi.di.sam.moveit.utils

object Constants{

    // Sensori
    const val ALPHA_FILTRO_PASSA_BASSO = 0.8f
    const val MAX_INTENSITA_ACC = 18.0f
    const val MAX_INTENSITA_GYRO = 15.0f
    const val PESO_ACC = 0.6f
    const val PESO_GYRO = 0.4f
    const val SOGLIA_MOVIMENTO = 0.05f
    const val MAX_DISTANZA_VETTORIALE = 8.0f /*TODO*/
    const val PASSO_GRIGLIA_MS = 450L
    const val FINESTRA_GRIGLIA_MS = 100L

    // Gioco
    const val INTERVALLO_CAMPIONAMENTO_MS = 40L
    // Punteggio grezzo
    const val PUNTEGGIO_GREZZO_MIN = 30.0
    const val PUNTEGGIO_GREZZO_MAX = 80.0

    //Tema

    const val TEMA_LIGHT = "light"
    const val TEMA_DARK = "dark"
    const val TEMA_SYSTEM = "system"

}