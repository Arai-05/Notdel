package cl.ara.notdel.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.viewmodel.NotebookViewModel
import cl.ara.notdel.viewmodel.EscaneoViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import cl.ara.notdel.viewmodel.EtapaEscaneo


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormArriendoScreen(
    notebook: Notebook,
    notebookViewModel: NotebookViewModel,
    escaneoViewModel: EscaneoViewModel,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current

    // ---- Variables del formulario ----
    var userNombre      by remember { mutableStateOf("") }
    var userEmail       by remember { mutableStateOf("") }
    var userTelefono    by remember { mutableStateOf("") }
    var userDireccion   by remember { mutableStateOf("") }
    var userEdad        by remember { mutableStateOf("") }
    var diasPrestamo    by remember { mutableStateOf("") }

    // ---- Variables de validacion ----
    var isIdentidadVerificada by remember { mutableStateOf(false) }
    var mensajeValidacion     by remember { mutableStateOf("") }

    // ---- Control de la camara y Flujo de 2 pasos ----
    var mostrarCamara    by remember { mutableStateOf(false) }
    var etapaActual      by remember { mutableStateOf(EtapaEscaneo.FRENTE) }
    var mensajeGuia      by remember { mutableStateOf("Escanea el frontis de tu cedula") }

    val isProcesando  by escaneoViewModel.isProcesando.collectAsState()


    // Permisos
    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                etapaActual   = EtapaEscaneo.FRENTE
                mensajeGuia   = "Escanea el frontis de tu cedula de identidad"
                mostrarCamara = true // Si dan permiso, muestra la camara
            } else {
                Toast.makeText(context, "Se necesita de una cámara para validar su identidad", Toast.LENGTH_SHORT).show()
            }
        }
    )

    if (mostrarCamara) {
        Dialog(
            onDismissRequest = { mostrarCamara = false },
            properties       = DialogProperties(usePlatformDefaultWidth = false) // Pantalla completa
        ) {
            CamaraCarnetScreen(
                textoInstruccion  = mensajeGuia,
                isProcesando      = isProcesando,
                onClose           = { mostrarCamara = false },
                onImagenCapturada = { imageProxy ->

                    escaneoViewModel.analizarImagen( // Cuando el usuario toma la foto se recibe la imagen aqui
                        imagenProxy   = imageProxy,
                        nombreUsuario = userNombre,
                        etapa         = etapaActual
                    ) { exito, mensaje ->

                        mensajeValidacion = mensaje // Para mostrar errores si falla

                        if (exito) {
                            if (etapaActual == EtapaEscaneo.FRENTE){
                                Toast.makeText(context, "Frontis escaneado correctamente", Toast.LENGTH_SHORT).show()
                                etapaActual = EtapaEscaneo.DORSO
                                mensajeGuia = "Ahora, Escanea el dorso de tu cedula de identidad"
                            } else {
                                isIdentidadVerificada = true
                                mostrarCamara = false // Cerramos la cámara si salió bien
                                Toast.makeText(context, "¡Identidad Verificada!", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            // Si falla, muestra el error y puede seguir intentando
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    // ---- Formulario ----

    Scaffold(
        topBar = { TopAppBar(title = { Text("Formulario de Arriendo") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Completa tus datos para arrendar el ${notebook.marca} ${notebook.modelo}.",
                style = MaterialTheme.typography.titleMedium
            )

            // Campo Nombre
            OutlinedTextField(
                value = userNombre,
                onValueChange = {
                    userNombre = it
                    userNombre = it
                    // Si cambia el nombre, debe validar de nuevo
                    if (isIdentidadVerificada) {
                        isIdentidadVerificada = false
                        mensajeValidacion = "Nombre cambiado. Valida nuevamente."
                    }
                },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                // Icono verde si ya está validado
                trailingIcon = {
                    if (isIdentidadVerificada) Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                }
            )

            OutlinedTextField(
                value = userEmail, onValueChange = { userEmail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = userTelefono, onValueChange = { userTelefono = it },
                label = { Text("Número Telefónico") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = userDireccion, onValueChange = { userDireccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = userEdad,
                    onValueChange = { if (it.all { char -> char.isDigit() }) userEdad = it },
                    label = { Text("Edad") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )

                OutlinedTextField(
                    value = diasPrestamo,
                    onValueChange = { if (it.all { char -> char.isDigit() }) diasPrestamo = it },
                    label = { Text("Días a arrendar") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }

            HorizontalDivider()

            // Tarjeta de validacion
            Text("Validación de Identidad", style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth())

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isIdentidadVerificada) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isIdentidadVerificada) {
                        // VISUAL: VALIDADO
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Identidad Confirmada", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    } else {
                        // VISUAL: PENDIENTE
                        Text("Se requiere foto de la cedula de identidad para continuar.", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (userNombre.length < 3) {
                                    Toast.makeText(context, "Primero escribe tu nombre completo arriba", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Verifica los permisos
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                        etapaActual   = EtapaEscaneo.FRENTE
                                        mensajeGuia   = "Escanea el frontis de tu cedula de identidad"
                                        mostrarCamara = true
                                    } else {
                                        launcherPermisos.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            enabled = !isProcesando
                        ) {
                            if (isProcesando) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analizando...")
                            } else {
                                Icon(Icons.Default.Badge, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Escanear Cedula de identidad")
                            }
                        }

                        if (!isIdentidadVerificada && mensajeValidacion.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text  = mensajeValidacion,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Se le solicita esto para una mayor seguridad a la hora de arrendar uno de nuestros dispositivos. Por nuestra seguridad," +
                                " Notdel guardara las imagenes de su cedula hasta que finalice el plazo de arriendo o cuando este sea cancelado.", style = MaterialTheme.typography.bodyMedium)

                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val ageInt = userEdad.toIntOrNull()
                    val diasInt = diasPrestamo.toIntOrNull()

                    // Validaciones del formulario
                    if (userNombre.isBlank() || userEmail.isBlank() || userTelefono.isBlank() || userDireccion.isBlank() || ageInt == null || diasInt == null) {
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else if (!userEmail.contains("@")) {
                        Toast.makeText(context, "El email debe tener @", Toast.LENGTH_SHORT).show()
                    } else if (ageInt < 18) {
                        Toast.makeText(context, "Debes ser mayor de edad", Toast.LENGTH_SHORT).show()
                    } else if (diasInt < 1) {
                        Toast.makeText(context, "Mínimo 1 día de arriendo", Toast.LENGTH_SHORT).show()
                    } else if (!isIdentidadVerificada) {
                        Toast.makeText(context, "Debes validar tu identidad con el carnet", Toast.LENGTH_LONG).show()
                    } else {
                        // Guardar datos en el viewmodel
                        notebookViewModel.cacheDataArriendo(
                            nombre = userNombre,
                            email = userEmail,
                            telefono = userTelefono,
                            direccion = userDireccion,
                            edad = ageInt,
                            totalDias = diasInt
                        )
                        onNextClick()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Siguiente")
            }
        }
    }
}