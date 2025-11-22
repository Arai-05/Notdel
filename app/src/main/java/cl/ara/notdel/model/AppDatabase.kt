package cl.ara.notdel.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notebook::class, Arriendo::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notebookDao(): NotebookDao
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
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}