package cl.ara.notdel.data.model

import com.google.gson.annotations.SerializedName

data class ArriendoRequest(

    @SerializedName("notebook_id")
    val notebookId: Int,

    @SerializedName("total_dias")
    val totalDias: Int,

    @SerializedName("fecha_renta")
    val fechaRenta: String,

    @SerializedName("user_nombre")
    val userNombre: String,

    @SerializedName("user_email")
    val userEmail: String,

    @SerializedName("user_telefono")
    val userTelefono: String,

    @SerializedName("user_direccion")
    val userDireccion: String,

    @SerializedName("user_edad")
    val userEdad: Int,
)