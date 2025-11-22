package cl.ara.notdel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import cl.ara.notdel.model.AppDatabase
import cl.ara.notdel.model.Arriendo
import cl.ara.notdel.model.Notebook
import cl.ara.notdel.repository.NotebookRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// data class simple para los datos temporales
private data class DataArriendoTemp(
    val userNombre: String,
    val userEmail: String,
    val userTelefono: String,
    val userDireccion: String,
    val userEdad: Int
)

class NotebookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotebookRepository
    val allNotebooks: LiveData<List<Notebook>>
    private var DataArriendoTemp: DataArriendoTemp? = null

    init {
        val db = AppDatabase.getDatabase(application)
        val notebookDao = db.notebookDao()
        val arriendoDao = db.arriendoDao()
        repository = NotebookRepository(notebookDao, arriendoDao)
        allNotebooks = repository.allNotebooks

        viewModelScope.launch {
            repository.populateInitialDataIfNeeded()
        }
    }

    fun cacheDataArriendo(
        nombre: String,
        email: String,
        telefono: String,
        direccion: String,
        edad: Int
    ) {
        DataArriendoTemp = DataArriendoTemp(
            nombre,
            email,
            telefono,
            direccion,
            edad
        )
    }

    fun getCachedUserNombre(): String? {
        // Devolver el nombre de usuario de los datos temporales
        return DataArriendoTemp?.userNombre
    }

    // Función llamada por la Pantalla de Acuerdo
    // Esta guarda en la base de datos.
    fun finalizarArriendo(notebookId: Int) = viewModelScope.launch {
        // Obtener los datos temporales
        val data = DataArriendoTemp ?: return@launch

        // Obtener la fecha actual
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaActual = sdf.format(Date())

        // Crer el objeto Arriendo COMPLETO
        val newArriendo = Arriendo(
            notebookId = notebookId,
            userNombre = data.userNombre,
            userEmail = data.userEmail,
            userTelefono = data.userTelefono,
            userDireccion = data.userDireccion,
            userEdad = data.userEdad,
            fechaRenta = fechaActual,
            terminosAceptados = true
        )

        // Insertar en la BD
        repository.addArriendo(newArriendo)

        // Modificar disponibilidad
        // Busca el notebook por ID en la lista observable actual
        val modificarNotebook = allNotebooks.value?.firstOrNull { it.id == notebookId }

        modificarNotebook?.let {
            // Crea una copia del notebook cambiando solo 'disponible' a false
            val notebookModificado = it.copy(disponible = false)

            // Llamamos al repositorio para actualizar el registro en la base de datos
            repository.updateNotebookDisponibilidad(notebookModificado)
        }

        // Limpiar los datos temporales
        DataArriendoTemp = null
    }

}