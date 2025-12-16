package cl.ara.notdel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.ara.notdel.viewmodel.NotebookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcuerdoScreen(
    notebookId: Int,
    viewModel: NotebookViewModel,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {

    val context = LocalContext.current

    val nombre = viewModel.getCachedUserNombre() ?: "el Arrendatario"

    val termsText = """
    
    TERMINOS Y CONDICIONES DE ARRIENDO - Notdel

    Al usar los servicios de NotDel SpA, el usuario acepta las siguientes condiciones relacionadas con el arriendo temporal de notebooks.

    1. USO DEL EQUIPO

    El notebook  **(${viewModel.allNotebooks.value?.firstOrNull { it.id == notebookId }?.modelo ?: "Notebook"})** arrendado por **${nombre}** debe utilizarse solo para fines personales, académicos o laborales lícitos.
    Está prohibido modificar el sistema, prestar el equipo a terceros o usarlo para actividades ilegales.

    2. RESPONSABILIDAD DEL USUARIO

    El usuario es responsable del cuidado, pérdida o daño del equipo durante el arriendo.
    Si el notebook se daña o se pierde, deberá cubrir su valor comercial o el costo de reparación.

    3. PAGOS Y GARANTIAS

    El arriendo tiene un costo establecido al momento de contratar el servicio.
    NotDel puede solicitar una garantía reembolsable, que se devolverá al entregar el equipo en buen estado.

    4. ENTREGA Y DEVOLUCION

    El notebook será entregado en la casa matriz en la dirección indicada.
    Debe devolverse en la fecha acordada, en las mismas condiciones. Los atrasos generan cargos adicionales.

    5. SOPORTE TECNICO

    Durante el periodo de arriendo, NotDel ofrece asistencia técnica remota o presencial.
    Si el problema fue causado por mal uso, el costo correrá por cuenta del usuario.

    6. PRIVACIDAD

    NotDel no accede ni almacena la información del usuario en el notebook.
    Se recomienda hacer copia de seguridad antes de devolver el equipo.

    7. TERMINACION

    NotDel puede finalizar el arriendo de forma anticipada si se incumplen los pagos o las condiciones de uso.

    8. ACEPTACION

    Al presionar "Aceptar", usted confirma que ha leído y acepta todos los términos y condiciones.
    """.trimIndent()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Contrato de Arriendo") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                "Por favor, lee y acepta los términos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenedor con scroll para el texto del contrato
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
            ) {
                Text(termsText, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre botones
            ) {
                // Boton de Rechazo
                OutlinedButton(
                    onClick  = {
                        Toast.makeText(context, "Arriendo Cancelado, sus datos fueron eliminados", Toast.LENGTH_SHORT).show()
                        onReject()
                    },
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("No Acepto")
                }

                Button(
                    onClick = {
                        viewModel.finalizarArriendo(notebookId)
                        onAccept()
                    },
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Text("Acepto")
                }
            }
        }
    }
}