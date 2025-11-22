package cl.ara.notdel.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebook")
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Clave primaria que se auto-incrementa
    val marca: String,
    val modelo: String,
    val precioDia: Int,
    val disponible: Boolean = true,

    // ESPECIFICACIONES TECNICAS
    val procesador: String,
    val ram: String,
    val almacenamiento: String,
    val pantalla: String,
    val gpu: String,
    val sistemaOperativo: String,
    val bateria: String,

    //Imagen
    val imagenResId: Int

)