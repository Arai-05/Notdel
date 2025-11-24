package cl.ara.notdel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.ui.screens.AcuerdoScreen
import cl.ara.notdel.ui.screens.FormArriendoScreen
import cl.ara.notdel.ui.screens.MisArriendosScreen
import cl.ara.notdel.ui.screens.NotebookDetailScreen
import cl.ara.notdel.ui.screens.NotebookListScreen
import cl.ara.notdel.ui.theme.NotdelTheme
import cl.ara.notdel.viewmodel.NotebookViewModel
import cl.ara.notdel.ui.screens.UbicacionRetiroScreen

enum class Screen {
    LIST,           // Pantalla principal con la lista de notebooks.
    DETAIL,         // Pantalla con los detalles de un notebook seleccionado.
    FORM,           // Pantalla de formulario para ingresar datos del arrendatario.
    AGREEMENT,      // Pantalla para revisar y aceptar el contrato.
    MAP,            // Pantalla para ver la ubicacion de los puntos de retiro del notebook
    MIS_ARRIENDOS   // Pantalla para ver los arriendos realizados
}

class MainActivity : ComponentActivity() {
    private val notebookViewModel: NotebookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotdelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Estado para almacenar el notebook que está actualmente seleccionado o en proceso de arriendo
                    var selectedNotebook by remember { mutableStateOf<Notebook?>(null) }

                    // Estado para controlar la pantalla actual
                    var currentScreen by remember { mutableStateOf(Screen.LIST) }

                    // Sistema de navegación basado en el estado 'currentScreen'
                    when (currentScreen) {

                        // PANTALLA DE LISTA
                        Screen.LIST -> {
                            NotebookListScreen(
                                viewModel = notebookViewModel,
                                onNotebookClick = { notebook ->
                                    selectedNotebook = notebook
                                    currentScreen = Screen.DETAIL
                                },

                                onIrMisArriendos = {
                                    currentScreen = Screen.MIS_ARRIENDOS
                                }
                            )
                        }

                        //  PANTALLA DE DETALLE
                        Screen.DETAIL -> {
                            // Al presionar 'atrás', vuelve a la lista
                            androidx.activity.compose.BackHandler {
                                selectedNotebook = null
                                currentScreen = Screen.LIST
                            }

                            NotebookDetailScreen(
                                notebook = selectedNotebook,
                                onArrendarClick = {
                                    currentScreen = Screen.FORM // Navega al formulario
                                }
                            )
                        }

                        // PANTALLA DE FORMULARIO
                        Screen.FORM -> {
                            // Al presionar 'atrás', vuelve al detalle
                            androidx.activity.compose.BackHandler {
                                currentScreen = Screen.DETAIL
                            }

                            // asegura de que haya un notebook para el formulario
                            selectedNotebook?.let { notebook ->
                                FormArriendoScreen(
                                    notebook = notebook,
                                    viewModel = notebookViewModel,
                                    onNextClick = {
                                        currentScreen = Screen.AGREEMENT // Navega al acuerdo
                                    }
                                )
                            }
                        }

                        // PANTALLA DE ACUERDO
                        Screen.AGREEMENT -> {
                            // Al presionar 'atrás', vuelve al formulario para editar datos
                            androidx.activity.compose.BackHandler {
                                currentScreen = Screen.FORM
                            }

                            // asegura de tener el ID del notebook para finalizar el arriendo
                            selectedNotebook?.let { notebook ->
                                AcuerdoScreen(
                                    notebookId = notebook.id,
                                    viewModel = notebookViewModel,
                                    onAccept = {
                                        selectedNotebook = null
                                        currentScreen = Screen.MAP
                                    }
                                )
                            }
                        }

                        // PANTALLA DE UBICACION
                        Screen.MAP -> {
                            // Si presiona "atrás", vuelve a la lista principal
                            androidx.activity.compose.BackHandler {
                                selectedNotebook = null
                                currentScreen = Screen.LIST
                            }

                            UbicacionRetiroScreen()
                        }

                        // PANTALLA DE MIS ARRIENDOS
                        Screen.MIS_ARRIENDOS -> {
                            // Si presiona "atrás" en el celular, vuelve al inicio
                            androidx.activity.compose.BackHandler {
                                currentScreen = Screen.LIST
                            }

                            MisArriendosScreen(
                                viewModel = notebookViewModel,
                                onVolverClick = {
                                    // Si presiona "Atras", vuelve al inicio
                                    currentScreen = Screen.LIST
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}