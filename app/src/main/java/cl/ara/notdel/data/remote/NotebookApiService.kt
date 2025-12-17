package cl.ara.notdel.data.remote

import cl.ara.notdel.data.model.ArriendoRequest
import cl.ara.notdel.data.model.ArriendoResponse
import cl.ara.notdel.data.model.EstadoArriendo
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.data.model.NotebookEstadoMini
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

// Sirve para decirle a Xano que solo se cambiara el estado
data class EstadoBody(val estado: EstadoArriendo)

interface NotebookApiService {

    // Traer los notebooks
    @GET("notebook")
    suspend fun  getAllNotebooks(): List<Notebook>

    // Trae solo los estados
    @GET("estado_notebook") //
    suspend fun obtenerSoloEstados(): Response<List<NotebookEstadoMini>>

    // Crear arriendo
    @POST("arriendo")
    suspend fun crearArriendo(@Body request: ArriendoRequest): Response<ArriendoResponse>

    // Actualizar disponibilidad de un notebook
    @PATCH("notebook/{id}")
    suspend fun actualizarDisponibilidad(
        @Path("id") id: Int,
        @Body body: EstadoBody
    ): Response<Notebook>

    //Eliminar Arriendo
    @DELETE("arriendo/{id}")
    suspend fun eliminarArriendo(@Path("id") idArriendoXano: Int): Response<Void>
}