package cl.ara.notdel.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cl.ara.notdel.data.model.AppDatabase
import cl.ara.notdel.data.model.Arriendo
import cl.ara.notdel.data.model.ArriendoRequest
import cl.ara.notdel.data.model.EstadoArriendo
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.data.remote.EstadoBody
import cl.ara.notdel.data.remote.RetrofitInstance
import cl.ara.notdel.repository.NotebookRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// data class para los datos temporales
data class DataArriendoTemp(
    val userNombre: String,
    val userEmail: String,
    val userTelefono: String,
    val userDireccion: String,
    val userEdad: Int,
    val totalDias: Int
)
class NotebookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotebookRepository

    // Lista de notebooks
    // _notebooks es privado y modificable. notebooks es publico y solo lectura
    private val _notebooks = MutableLiveData<List<Notebook>>()
    val allNotebooks: LiveData<List<Notebook>> = _notebooks

    // Estado de carga (circulo de carga)
    private val _isCargando = MutableLiveData(false)
    val isCargando: LiveData<Boolean> = _isCargando

    // Estado de error (mostar un Toast si falla el internet)
    private val _mensajeError = MutableLiveData<String?>(null)
    val mensajeError: LiveData<String?> = _mensajeError

    // Datos temporales del formulario
    private var DataArriendoTemp: DataArriendoTemp? = null

    init {
        val db = AppDatabase.getDatabase(application)
        val arriendoDao = db.arriendoDao()

        // Conexion con xano (api) y el room de arriendo
        repository = NotebookRepository(RetrofitInstance.api, arriendoDao)

        cargarNotebooks(mostrarCarga = true)
        iniciarAutoRefresco()
    }

    fun cargarNotebooks(mostrarCarga: Boolean = true) {
        viewModelScope.launch {
            if (mostrarCarga) _isCargando.value = true
            try {
                // Llamar a la api desde el repo
                val lista = repository.obtenerNotebooksApi()
                _notebooks.value = lista
                _mensajeError.value = null
            } catch (e: Exception) {
                _mensajeError.value = "Error de conexion: ${e.message}"
            } finally {
                _isCargando.value = false
            }
        }
    }

    private fun iniciarAutoRefresco() = viewModelScope.launch {
        while (isActive) { // Mientras el ViewModel esté vivo (la app corriendo)
            delay(4000) // Espera 4 segundos

            // Recarga los datos
            cargarNotebooks(mostrarCarga = false)
        }
    }

    // Guardar datos temporales del formulario
    fun cacheDataArriendo(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        edad: Int,
        totalDias: Int
    ) {
        DataArriendoTemp = DataArriendoTemp(
            nombre,
            email,
            telefono,
            direccion,
            edad,
            totalDias
        )
    }

    // Esta funcion es para poder trabajar con los datos para el ResumenArriendoScreen
    fun obtenerDatosArriendo(): DataArriendoTemp? {
        return DataArriendoTemp
    }

    fun getCachedUserNombre(): String? {
        // Devolver el nombre de usuario de los datos temporales
        return DataArriendoTemp?.userNombre
    }

    fun limpiarDatosUsuario() {
        DataArriendoTemp = null
    }

    // Finalizar Arriendo, llamado por la pantalla de acuerdo
    fun finalizarArriendo(notebookId: Int) = viewModelScope.launch {

        // Obtener los datos temporales
        val data = DataArriendoTemp ?: return@launch
        _isCargando.value = true

        try {
            // Obtener la fecha actual
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaActual = sdf.format(Date())

            // Crear el request para xano
            val requestXano = ArriendoRequest(
                notebookId = notebookId,
                totalDias = data.totalDias,
                fechaRenta = fechaActual,
                userNombre = data.userNombre,
                userEmail = data.userEmail,
                userTelefono = data.userTelefono,
                userDireccion = data.userDireccion,
                userEdad = data.userEdad
            )

            // Buscar notebook original para guardarlo en el historial local
            val notebookSeleccionado = _notebooks.value?.find { it.id == notebookId }

            if (notebookSeleccionado != null) {
                // Llamar al repo para hacer un post a xano + insert en room
                repository.confirmarArriendo(requestXano, notebookSeleccionado)

                // Recargar notebooks para que el notebooks aparezca como no disponible
                cargarNotebooks()

                // Limpiar datos temporales
                limpiarDatosUsuario()
            } else {
                _mensajeError.value = "Error: No se encontro el notebook seleccionado"
            }
        } catch (e: Exception) {
            _mensajeError.value = "Error de conexion: ${e.message}"
        } finally {
            _isCargando.value = false
        }
    }
    // Mis arriendos
    val misArriendos = repository.obtenerMisArriendos().asLiveData()

    // Devolver arriendo
    fun devolverArriendo(arriendo: Arriendo) = viewModelScope.launch {
        _isCargando.value = true
        try {
            // llamar al repo
            repository.cancelarArriendo(arriendo)

            // recarga la lista de notebooks para que vuelva a salir disponible
            cargarNotebooks()
        } catch (e: Exception) {
            _mensajeError.value = "Error al devolver arriendo: ${e.message}"
        } finally {
            _isCargando.value = false
        }
    }

    // Cuando el usuario ingresa el codigo 600900 correctamente
    fun confirmarRetiroExitoso(arriendo: Arriendo) = viewModelScope.launch {
        _isCargando.value = true
        try {
            // Llamamos a Xano para cambiar el estado a ARRENDADO (Azul)
            val estadoBody = EstadoBody(EstadoArriendo.ARRENDADO)
            repository.actualizarEstadoEnXano(arriendo.notebookId, estadoBody)

            // Recargamos la lista para que se actualice la UI
            cargarNotebooks()
        } catch (e: Exception) {
            _mensajeError.value = "Error al confirmar retiro: ${e.message}"
        } finally {
            _isCargando.value = false
        }
    }

    // Bloquea el notebook para que nadie mas lo tome cuando alguien este arrendandolo
    fun reservarNotebook(id: Int) = viewModelScope.launch {
        try {
            val body = EstadoBody(EstadoArriendo.RESERVANDO)
            repository.actualizarEstadoEnXano(id, body)
        } catch (e: Exception) {
            Log.e("API", "No se pudo reservar: ${e.message}")
        }
    }

    // Libera el note si se sale del formulario
    fun liberarNotebook(id: Int) = viewModelScope.launch {
        try {
            val body = EstadoBody(EstadoArriendo.DISPONIBLE)
            repository.actualizarEstadoEnXano(id, body)
        } catch (e: Exception) {
            Log.e("API", "No se pudo liberar: ${e.message}")
        }
    }


}