package cl.ara.notdel.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "arriendo",)
data class Arriendo(
    @PrimaryKey(autoGenerate = true)
    val rentalId: Int = 0,
    val notebookId: Int,

    // Id de xano
    val idXano: Int,

    // Campos del formulario
    val userNombre: String,
    val userEmail: String,
    val userTelefono: String,
    val userDireccion: String,
    val userEdad: Int,

    val fechaRenta: String,
    val totalDias: Int,
    val terminosAceptados: Boolean = true // constancia de que aceptó
)