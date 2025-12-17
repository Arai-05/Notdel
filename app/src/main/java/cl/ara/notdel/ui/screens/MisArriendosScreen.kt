package cl.ara.notdel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.ara.notdel.data.model.Arriendo
import cl.ara.notdel.data.model.EstadoArriendo
import cl.ara.notdel.data.model.Notebook
import coil.compose.AsyncImage
import cl.ara.notdel.viewmodel.NotebookViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisArriendosScreen(
    viewModel: NotebookViewModel,
    onVolverClick: () -> Unit
) {
    val listaArriendos    by viewModel.misArriendos.observeAsState(initial = emptyList())
    val catalogoNotebooks by viewModel.allNotebooks.observeAsState(initial = emptyList())

    // Estados para el codigo de retiro
    var showDialogCodigo               by remember { mutableStateOf(false) }
    var codigoIngresado                by remember { mutableStateOf("") }
    var arriendoSeleccionadoParaRetiro by remember { mutableStateOf<Arriendo?>(null) }
    var mensajeErrorCodigo by remember { mutableStateOf<String?>(null) }

    // Recargar datos al entrar
    LaunchedEffect(Unit) { viewModel.cargarNotebooks() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Arriendos") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (listaArriendos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No tienes arriendos activos.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaArriendos) { arriendo ->
                    val infoNotebook = catalogoNotebooks.find { it.id == arriendo.notebookId }

                    ArriendoItemCard(
                        arriendo = arriendo,
                        notebookInfo = infoNotebook,
                        onDevolver = { tipoAccion ->
                            when(tipoAccion) {
                                "CANCELAR" -> viewModel.devolverArriendo(arriendo) // Cancelar reserva
                                "DEVOLVER" -> viewModel.devolverArriendo(arriendo) // Devolver equipo
                                "CONFIRMAR_RETIRO" -> {
                                    // Abrimos el dialog para pedir codigo
                                    arriendoSeleccionadoParaRetiro = arriendo
                                    codigoIngresado = "" // limpiar campo
                                    mensajeErrorCodigo = null
                                    showDialogCodigo = true
                                }
                            }
                        }
                    )
                }
            }
        }

        // Ventana para el codigo
        if (showDialogCodigo) {
            AlertDialog(
                onDismissRequest = { showDialogCodigo = false },
                title = { Text("Confirmar Retiro") },
                text = {
                    Column {
                        Text("Ingresa el código para confirmar el retiro:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = codigoIngresado,
                            onValueChange = { codigoIngresado = it },
                            label = { Text("Código de 6 dígitos") },
                            singleLine = true,
                            isError = mensajeErrorCodigo != null
                        )
                        if (mensajeErrorCodigo != null) {
                            Text(mensajeErrorCodigo!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Validacion del codigo
                        if (codigoIngresado == "600900") {
                            // Llama al ViewModel para cambiar a ARRENDADO
                            arriendoSeleccionadoParaRetiro?.let { arriendo ->
                                viewModel.confirmarRetiroExitoso(arriendo)
                            }
                            showDialogCodigo = false
                        } else {
                            mensajeErrorCodigo = "Código incorrecto. Intenta de nuevo."
                        }
                    }) {
                        Text("Validar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialogCodigo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ArriendoItemCard(
    arriendo: Arriendo,
    notebookInfo: Notebook?, // Puede ser nulo si no cargó el catálogo
    onDevolver: (String) -> Unit
) {
    // Si no ha cargado la info del note, asume un estado por defecto para que no falle
    val estadoActual = notebookInfo?.estado ?: EstadoArriendo.ARRENDADO

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // FOTO
                if (notebookInfo != null) {
                    AsyncImage(
                        model = notebookInfo.imagenUrl,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Placeholder si no hay info visual
                    Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Nombre del modelo
                    Text(
                        text = notebookInfo?.modelo ?: "Cargando...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Fecha
                    Text(
                        text = "Desde: ${arriendo.fechaRenta}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    // Dias
                    Text(
                        text = "${arriendo.totalDias} días contratados",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Texto de estado
                    Column {
                        Text(
                            text = "Estado actual:",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )

                        // Que texto mostrar segun el estado
                        val (textoEstado, colorEstado) = when (estadoActual) {
                            EstadoArriendo.POR_RETIRAR -> "Listo para Retiro" to Color(0xFFFF9800) // Naranjo
                            EstadoArriendo.ARRENDADO -> "En tu poder (En uso)" to Color(0xFF2196F3)   // Azul
                            EstadoArriendo.EN_DEVOLUCION -> "Devolución en proceso" to Color(
                                0xFF9C27B0
                            ) // Morado
                            else -> "Procesando..." to Color.Gray
                        }

                        Text(
                            text = textoEstado,
                            style = MaterialTheme.typography.labelLarge,
                            color = colorEstado,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Boton de accion que cambia segun el estado (derecha)
                    when (estadoActual) {
                        EstadoArriendo.POR_RETIRAR, EstadoArriendo.DISPONIBLE, EstadoArriendo.RESERVANDO -> {
                            Row {
                                // Boton Cancelar
                                OutlinedButton(
                                    onClick = { onDevolver("CANCELAR") },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                    modifier = Modifier.padding(end = 8.dp)
                                ) {
                                    Text("Cancelar")
                                }

                                // Boton Confirmar Retiro
                                Button(
                                    onClick = { onDevolver("CONFIRMAR_RETIRO") },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)) // Naranjo
                                ) {
                                    Text("Retirar")
                                }
                            }
                        }

                        EstadoArriendo.ARRENDADO -> {
                            // Si ya lo tiene puede devolverlo
                            Button(onClick = { onDevolver("DEVOLVER") }) {
                                Text("Devolver")
                            }
                        }
                        else -> { }
                    }
                }
        }
    }
}