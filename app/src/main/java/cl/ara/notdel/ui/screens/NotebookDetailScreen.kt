package cl.ara.notdel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.ara.notdel.data.model.Notebook
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookDetailScreen(
    notebook: Notebook?,
    onArrendarClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalles del Producto") })
        }
    ) { paddingValues ->
        if (notebook == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No se pudo cargar el notebook.")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()), // Habilita el scroll
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = notebook.imagenUrl,
                    contentDescription = notebook.modelo,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Fit
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ){
                    // Marca y Modelo
                    Text(
                        text = "${notebook.marca} ${notebook.modelo}",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Precio
                    Text(
                        text = "Arriendo Diario: \$${notebook.precioDia}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    // Separador y Título de Especificaciones
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(modifier = Modifier.height(1.dp))
                    Text(
                        text = "Especificaciones Técnicas",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    // Especificaciones
                    SpecRow(label = "Procesador", value = notebook.procesador)
                    SpecRow(label = "RAM", value = notebook.ram)
                    SpecRow(label = "Almacenamiento", value = notebook.almacenamiento)
                    SpecRow(label = "Pantalla", value = notebook.pantalla)
                    SpecRow(label = "GPU", value = notebook.gpu.joinToString(separator = "\n") { "• $it" })
                    SpecRow(label = "Batería", value = notebook.bateria)
                    SpecRow(label = "Sistema Operativo", value = notebook.sistemaOperativo)

                    Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

                    // Botón de Acción
                    Button(
                        onClick = {
                            onArrendarClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        Text("Arrendar Ahora", modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}

// Composable auxiliar para un mejor diseño de la lista de specs
@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}