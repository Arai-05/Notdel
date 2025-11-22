package cl.ara.notdel.repository

import androidx.lifecycle.LiveData
import cl.ara.notdel.model.Notebook
import cl.ara.notdel.model.NotebookDao

import cl.ara.notdel.R
import cl.ara.notdel.model.Arriendo
import cl.ara.notdel.model.ArriendoDao

class NotebookRepository(
    private val notebookDao: NotebookDao,
    private val arriendoDao: ArriendoDao
)
{
    val allNotebooks: LiveData<List<Notebook>> = notebookDao.getAllNotebooks()

    suspend fun addArriendo(Arriendo: Arriendo) {
        arriendoDao.insertarArriendo(Arriendo)
    }

    suspend fun updateNotebookDisponibilidad(notebook: Notebook) {
        notebookDao.updateNotebook(notebook)
    }
    suspend fun populateInitialDataIfNeeded() {
        if (notebookDao.getCount() == 0) {
            val notebooksIniciales = listOf(
                Notebook(
                    marca = "ASUS",
                    modelo = "ROG Strix Scar 18 G835LX-SA135W",
                    procesador = "Intel Core Ultra 9 275HX (2100 MHz - 5400 MHz)",
                    ram = "64 GB DDR5 (5600 MHz)",
                    almacenamiento = "SSD 2 TB",
                    pantalla = "LED 18.0\" (2560x1600) / 240 Hz",
                    gpu = "NVIDIA GeForce RTX 5090 (24 GB)",
                    sistemaOperativo = "Microsoft Windows 11 Home",
                    bateria = "90000 mWh",
                    precioDia = 65000,
                    imagenResId = R.drawable.notdel_rog_strix_scar_18
                ),
                Notebook(
                    marca = "ASUS",
                    modelo = "TUF Gaming A15 FA507NVR-LP005W",
                    procesador = "AMD Ryzen 7 7435HS (3100 MHz - 4500 MHz)",
                    ram = "16 GB DDR5 (4800 MHz)",
                    almacenamiento = "SSD 512 GB",
                    pantalla = "LED 15.6\" (1920x1080) / 144 Hz",
                    gpu = "NVIDIA GeForce RTX 4060 (8 GB)",
                    sistemaOperativo = "Microsoft Windows 11 Home",
                    bateria = "94000 mWh",
                    precioDia = 25000,
                    imagenResId = R.drawable.notdel_asus_tuf_gaming_a15
                ),
                Notebook(
                    marca = "HP",
                    modelo = "15-DW1512LA",
                    procesador = "Intel Pentium Silver N5030 (1100 MHz - 3100 MHz)",
                    ram = "4 GB DDR4 (2400 MHz)",
                    almacenamiento = "SSD 128 GB",
                    pantalla = "LED 15.6\" (1366x768) / 60 Hz",
                    gpu = "Intel UHD Graphics 605 (Integrada)",
                    sistemaOperativo = "Microsoft Windows 10 Home",
                    bateria = "41000 mWh",
                    precioDia = 7000,
                    imagenResId = R.drawable.notdel_hp_15
                ),
                Notebook(
                    marca = "Lenovo",
                    modelo = "IdeaPad Slim 3 14IRH10",
                    procesador = "Intel Core i5-13420H (1500 MHz - 4600 MHz)",
                    ram = "8 GB DDR5 (4800 MHz)",
                    almacenamiento = "SSD 512 GB",
                    pantalla = "LED 14.0\" (1920x1200) / 60 Hz",
                    gpu = "Intel UHD Graphics Xe G4 48EUs (Integrada)",
                    sistemaOperativo = "Microsoft Windows 11 Home",
                    bateria = "60000 mWh",
                    precioDia = 11000,
                    imagenResId = R.drawable.notdel_lenovo_ideapad_slim
                )
            )
            notebookDao.insertAll(notebooksIniciales)
        }
    }
}
