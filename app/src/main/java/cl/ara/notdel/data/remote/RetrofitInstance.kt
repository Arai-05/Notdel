package cl.ara.notdel.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:2nvAVXKA/"

    val api: NotebookApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) //Convierte el json a clases de kotlin
            .build()
            .create(NotebookApiService::class.java)
    }
}