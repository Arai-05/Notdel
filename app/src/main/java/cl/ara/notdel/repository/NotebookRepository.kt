package cl.ara.notdel.repository

import android.util.Log
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.data.model.Arriendo
import cl.ara.notdel.data.model.ArriendoDao
import cl.ara.notdel.data.model.ArriendoRequest
import cl.ara.notdel.data.remote.NotebookApiService
import kotlinx.coroutines.flow.Flow

class NotebookRepository(
    private val api: NotebookApiService,
    private val arriendoDao: ArriendoDao
)
{
    // catalogo de notebooks
    suspend fun obtenerNotebooksApi(): List<Notebook> {
        try {
            return api.getAllNotebooks()
        } catch (e: Exception) {
            Log.e("INFO_API","Error al obtener los notebooks de la API: ${e}")
            e.printStackTrace()
            return emptyList()
        }
    }

    // historial de mis arriendos
    fun obtenerMisArriendos(): Flow<List<Arriendo>> {
        return arriendoDao.obtenerMisArriendos()
    }

    // confirmar arriendo
    suspend fun confirmarArriendo(
        requestDto: ArriendoRequest, // datos para mandarlos a xano
        notebookInfo: Notebook // datos para guardar en la copia local
    ) {
        // hacer el post
        val respuesta = api.crearArriendo(requestDto)

        if (respuesta.isSuccessful && respuesta.body() != null) {

            // tomar id real del xano
            val idRemotoXano = respuesta.body()!!.id

            // actualizar la disponibilidad del notebook
            api.actualizarDisponibilidad(requestDto.notebookId, mapOf("disponible" to false))

            // guardar el arriendo en la copia local
            val nuevoArriendoLocal = Arriendo(
                idXano          = idRemotoXano,
                notebookId      = requestDto.notebookId,
                totalDias       = requestDto.totalDias,
                fechaRenta      = requestDto.fechaRenta,

                // datos del usuario
                userNombre      = requestDto.userNombre,
                userEmail       = requestDto.userEmail,
                userTelefono    = requestDto.userTelefono,
                userDireccion   = requestDto.userDireccion,
                userEdad        = requestDto.userEdad,

                terminosAceptados = true
            )
            arriendoDao.insertarArriendo(nuevoArriendoLocal)

        } else {
            throw Exception("Error al procesar el arriendo en el servidor")
        }
    }

    suspend fun cancelarArriendo(arriendo: Arriendo) {

        // borrarlo de xano con el id remoto
        val respuesta = api.eliminarArriendo(arriendo.idXano)

        if (respuesta.isSuccessful) {
            // cambiar la disponibilidad del notebook a true
            api.actualizarDisponibilidad(arriendo.notebookId, mapOf("disponible" to true))

            // borrarlo de la base de datos local
            arriendoDao.eliminarArriendo(arriendo)
        } else {
            throw Exception("No se pudo cancelar el arriendo en el servidor")
        }
    }
}
