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
import cl.ara.notdel.model.Notebook
import cl.ara.notdel.viewmodel.NotebookViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormArriendoScreen(
    notebook: Notebook,
    viewModel: NotebookViewModel,
    onNextClick: () -> Unit
) {
    var userNombre by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userTelefono by remember { mutableStateOf("") }
    var userDireccion by remember { mutableStateOf("") }
    var userEdad by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Formulario") }) }
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

            OutlinedTextField(
                value = userNombre, onValueChange = { userNombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
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

            OutlinedTextField(
                value = userEdad, onValueChange = { userEdad = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val ageInt = userEdad.toIntOrNull()

                    if (userNombre.isBlank() || userEmail.isBlank() || userTelefono.isBlank() || userDireccion.isBlank() || ageInt == null) {
                        Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    } else if (ageInt < 18) {
                        Toast.makeText(context, "Debes ser mayor de edad para arrendar", Toast.LENGTH_SHORT).show()
                    } else {

                        viewModel.cacheDataArriendo(
                            nombre = userNombre,
                            email = userEmail,
                            telefono = userTelefono,
                            direccion = userDireccion,
                            edad = ageInt
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