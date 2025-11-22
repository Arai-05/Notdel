package cl.ara.notdel.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "arriendo",
    foreignKeys = [
        ForeignKey(
            entity = Notebook::class,
            parentColumns = ["id"],
            childColumns = ["notebookId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Arriendo(
    @PrimaryKey(autoGenerate = true)
    val rentalId: Int = 0,
    val notebookId: Int,

    // Campos del formulario
    val userNombre: String,
    val userEmail: String,
    val userTelefono: String,
    val userDireccion: String,
    val userEdad: Int,


    val fechaRenta: String,
    val terminosAceptados: Boolean = true // constancia de que aceptó
)