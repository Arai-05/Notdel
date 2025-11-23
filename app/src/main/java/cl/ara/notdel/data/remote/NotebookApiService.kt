package cl.ara.notdel.data.remote

import cl.ara.notdel.data.model.ArriendoRequest
import cl.ara.notdel.data.model.ArriendoResponse
import cl.ara.notdel.data.model.Notebook
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path


interface NotebookApiService {

    // Traer los notebooks
    @GET("notebook")
    suspend fun  getAllNotebooks(): List<Notebook>

    // Crear arriendo
    @POST("arriendo")
    suspend fun crearArriendo(@Body request: ArriendoRequest): Response<ArriendoResponse>

    // Actualizar disponibilidad de un notebook
    @PATCH("notebook/{id}")
    suspend fun actualizarDisponibilidad(
        @Path("id") id: Int,
        @Body body: Map<String, Boolean>
    ): Response<Void>

    //Eliminar Arriendo
    @DELETE("arriendo/{id}")
    suspend fun eliminarArriendo(@Path("id") idArriendoXano: Int): Response<Void>
}