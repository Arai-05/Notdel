package cl.ara.notdel.data.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Arriendo::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun arriendoDao(): ArriendoDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notdel_database"
                ).fallbackToDestructiveMigration().build() // Si se cambia la base de datos, la borra y la crea de nuevo
                INSTANCE = instance
                instance
            }
        }
    }
}