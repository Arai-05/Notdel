package cl.ara.notdel.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.ara.notdel.data.model.Arriendo
import cl.ara.notdel.data.model.Notebook
import coil.compose.AsyncImage
import cl.ara.notdel.viewmodel.NotebookViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisArriendosScreen(
    viewModel: NotebookViewModel,
    onVolverClick: () -> Unit // botón de atras
) {
    // Obtenemos dos listas
    val listaArriendos by viewModel.misArriendos.observeAsState(initial = emptyList())
    val catalogoNotebooks by viewModel.allNotebooks.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Arriendos Activos") },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver atrás"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (listaArriendos.isEmpty()) {
            // cuando este vacio
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes arriendos activos.")
            }
        } else {
            // Lista de arriendos
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listaArriendos) { arriendo ->

                    val infoNotebook = catalogoNotebooks.find { it.id == arriendo.notebookId }

                    ArriendoItemCard(
                        arriendo = arriendo,
                        notebookInfo = infoNotebook,
                        onDevolver = {
                            viewModel.devolverArriendo(arriendo)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ArriendoItemCard(
    arriendo: Arriendo,
    notebookInfo: Notebook?, // Puede ser nulo si no cargó el catálogo
    onDevolver: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // FOTO (Si tenemos la info del catálogo)
            if (notebookInfo != null) {
                AsyncImage(
                    model = notebookInfo.imagenUrl,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            } else {
                // Placeholder si no hay info visual
                Box(modifier = Modifier.size(80.dp), contentAlignment = Alignment.Center) {
                    Text("?", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // NOMBRE DEL MODELO
                Text(
                    text = notebookInfo?.modelo ?: "Equipo ID: ${arriendo.notebookId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // FECHA DE RENTA
                Text(
                    text = "Desde: ${arriendo.fechaRenta}",
                    style = MaterialTheme.typography.bodyMedium
                )

                // DÍAS
                Text(
                    text = "Duración: ${arriendo.totalDias} días",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // BOTÓN DEVOLVER
            IconButton(onClick = onDevolver) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Devolver",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}