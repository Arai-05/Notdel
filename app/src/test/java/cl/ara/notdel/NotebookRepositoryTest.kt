package cl.ara.notdel.repository

import cl.ara.notdel.data.remote.NotebookApiService
import cl.ara.notdel.data.model.ArriendoDao
import cl.ara.notdel.data.model.EstadoArriendo
import cl.ara.notdel.data.model.Notebook
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class NotebookRepositoryTest {

    // API y DAO falsas
    private val api = mockk<NotebookApiService>()
    private val dao = mockk<ArriendoDao>()

    // Instancia el repositorio real pasandole los falsos
    private val repository = NotebookRepository(api, dao)

    @Test
    fun obtenerNotebooksTest() = runTest {
        // Lista falsa de notebooks que simula venir de Internet
        val listaFalsa = listOf(
            Notebook(
                id = 1,
                marca = "Asus",
                modelo = "TUF Gaming",
                estado = EstadoArriendo.DISPONIBLE,
                procesador = "Intel Core i7",
                ram = "16 GB",
                almacenamiento = "512 GB SSD",
                pantalla = "15.6 FHD",
                gpu = listOf("NVIDIA RTX 3050"),
                bateria = "90Wh",
                sistemaOperativo = "Windows 11",
                precioDia = 15000,
                imagenUrl = "https://ejemplo.com/asus.jpg"
            ),
            Notebook(
                id = 2,
                marca = "Lenovo",
                modelo = "Legion 5",
                estado = EstadoArriendo.ARRENDADO,
                procesador = "AMD Ryzen 7",
                ram = "32 GB",
                almacenamiento = "1 TB SSD",
                pantalla = "17.3 4K",
                gpu = listOf("NVIDIA RTX 4060", "Integrada AMD"),
                bateria = "80Wh",
                sistemaOperativo = "Linux",
                precioDia = 20000,
                imagenUrl = "https://ejemplo.com/lenovo.jpg"
            )
        )

        coEvery { api.getAllNotebooks() } returns listaFalsa


        // Llama a la función del repositorio
        val resultado = repository.obtenerNotebooksApi()

        // Verifica que trajo 2 elementos
        resultado.size shouldBe 2

        // Verifica que el primer elemento es el Asus TUF
        resultado[0].modelo shouldBe "TUF Gaming"
        resultado[0].precioDia shouldBe 15000

        // Verifica que la GPU (que es una lista) se guardo bien
        resultado[1].gpu[0] shouldBe "NVIDIA RTX 4060"

        // Verifica que llamo a la API
        coVerify(exactly = 1) { api.getAllNotebooks() }
    }
}