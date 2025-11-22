package cl.ara.notdel.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.ara.notdel.model.Notebook
import cl.ara.notdel.viewmodel.NotebookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookListScreen(
    viewModel: NotebookViewModel,
    onNotebookClick: (Notebook) -> Unit // Función para manejar el clic en un item
) {

    // Cada vez que la lista en la BD cambie, esta variable se actualizará y la UI se redibujará.
    val notebooks by viewModel.allNotebooks.observeAsState(initial = emptyList())

    // SCROLL Y BARRA SUPERIOR
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "¡Bienvenido a NotDel!",
                        modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->

        // LISTA PRINCIPAL
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre ítems
        ) {

            item {
                // TITULAR DE LA LISTA
                Text(
                    text = "Equipos Disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                )
            }

            items(notebooks) { notebook ->
                NotebookItem(notebook = notebook, onNotebookClick = onNotebookClick)
            }
        }
    }
}


// Este es el Composable para un solo item de la lista.
@Composable
fun NotebookItem(
    notebook: Notebook,
    onNotebookClick: (Notebook) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNotebookClick(notebook) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen del Notebook
            Image(
                painter = painterResource(id = notebook.imagenResId),
                contentDescription = notebook.modelo,
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Marca y Modelo
                Text(
                    text = notebook.marca,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = notebook.modelo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Precio
                Text(
                    text = "$${notebook.precioDia}/día",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Indicador de disponibilidad
                Spacer(modifier = Modifier.height(4.dp))
                if (notebook.disponible) {
                    Text(
                        text = "DISPONIBLE",
                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50), // Verde
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Text(
                        text = "ARRENDADO",
                        color = MaterialTheme.colorScheme.error, // Rojo
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}