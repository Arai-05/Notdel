package cl.ara.notdel.model

import androidx.lifecycle.LiveData
import androidx.room.*
@Dao
interface NotebookDao {

    @Query("SELECT * FROM notebook")
    fun getAllNotebooks(): LiveData<List<Notebook>>

    // Inserta un nuevo notebook. Si ya existe uno con la misma clave primaria, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notebooks: List<Notebook>)

    @Query("SELECT COUNT(*) FROM notebook")
    suspend fun getCount(): Int

    @Update
    suspend fun updateNotebook(notebook: Notebook)
}