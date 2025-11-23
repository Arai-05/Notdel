package cl.ara.notdel.data.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArriendoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarArriendo(arriendo: Arriendo)

    @Query("SELECT * FROM Arriendo ORDER BY rentalId DESC")
    suspend fun obtenerMisArriendos(): List<Arriendo>

    @Delete
    suspend fun eliminarArriendo(arriendo: Arriendo)
}