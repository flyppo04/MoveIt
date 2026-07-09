package it.unipi.di.sam.moveit.data

import android.content.Context //context rappresenta ambiente applicazione, per dire a Room dove salvare db
import androidx.room.Database //per @Database indica a Room la classe astratta è il punto di accesso al dbù
import androidx.room.RoomDatabase //classe che noi estendiamo per avere db
import androidx.room.Room // classe factory per creare/recuperare db

@Database(
    entities   = [Game::class],
    version    = 1,
    exportSchema = false
)
// abstract perchè sara Room a creare i metodi reali per me, io li indico solo in modo astratto
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDAO(): GameDAO

    // companion object serve ad avere metodi e proprità statiche (appartenenti alla classe e non all'istanza)
    // come gli ststic in java, qui la usiamo perchè è il modo per prendere dall'estreno il db
    // in modo che quetso sia unico (Singleton)
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            // ?: elivs operetor , se quello a sx è null fai quello a dx
            // synchronized ritorna l'ultima espressione del blocco
            return INSTANCE ?: synchronized(this){
                val instance = INSTANCE ?: Room // rieffettuo il controllo, istance potrebbe esser stato inizializzato da un altro thread
                    .databaseBuilder(
                    context.applicationContext, // se mettevo Activity context causavo memory leak
                    AppDatabase::class.java, // prende classe AppDat. e la traforam in classe java (Room usa java)
                    "moveit_database.db" // nome file che verrà generato
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

}
