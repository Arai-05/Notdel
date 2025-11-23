package cl.ara.notdel.data.model

import com.google.gson.annotations.SerializedName


data class Notebook(
    val id: Int,
    val marca: String,
    val modelo: String,
    val disponible: Boolean = true,

    // ESPECIFICACIONES TECNICAS
    val procesador: String,
    val ram: String,
    val almacenamiento: String,
    val pantalla: String,
    val gpu: String,
    val bateria: String,

    // Serializacion de variables
    @SerializedName("sistema_operativo")
    val sistemaOperativo: String,

    @SerializedName("precio_dia")
    val precioDia: Int,

    //Imagen
    @SerializedName("imagen")
    val imagenUrl: String
)