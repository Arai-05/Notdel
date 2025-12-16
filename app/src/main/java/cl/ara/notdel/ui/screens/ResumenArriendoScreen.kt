package cl.ara.notdel.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.ara.notdel.data.model.Notebook
import cl.ara.notdel.viewmodel.NotebookViewModel
import coil.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenArriendoScreen(
    notebook: Notebook,
    notebookViewModel: NotebookViewModel,
    onConfirmar: () -> Unit
) {
    val datos = notebookViewModel.obtenerDatosArriendo()

    // Si por alguna razon los datos son nulos (no debería pasar), muestra esto por defecto
    val nombreUsuario    = datos?.userNombre ?: "Desconocido"
    val direccionUsuario = datos?.userDireccion ?: "Sin dirección"
    val diasArriendo     = datos?.totalDias ?: 1

    // Calculos de precio
    val precioDiario = notebook.precioDia
    val totalPagar   = precioDiario * diasArriendo

    // Formateador de moneda a CLP
    val localCLP = Locale.forLanguageTag("es-CL")
    val formaCLP = NumberFormat.getCurrencyInstance(localCLP)

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Resumen de Arriendo") }
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Para pantallas pequeñas
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Seccion del notebook junto a la foto
            Card (
                modifier  = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White)
            ){
                Column (
                    modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
                ){
                    AsyncImage(
                        model              = notebook.imagenUrl,
                        contentDescription = notebook.modelo,
                        contentScale       = ContentScale.Fit,
                        modifier           = Modifier.height(150.dp).fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text        = "${notebook.marca} ${notebook.modelo}",
                        style       = MaterialTheme.typography.headlineSmall,
                        fontWeight  = FontWeight.Bold
                    )
                }
            }

            // Datos del cliente
            Card (
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text  = "Arrendado por:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier           = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text       = nombreUsuario, style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier           = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text       = direccionUsuario, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            // Detalle del pago
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = Color(0XFFE3F2FD))
            ) {
                Column (modifier = Modifier.padding(16.dp)){
                    Row(
                       modifier              = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text("Valor diario:")
                        Text(formaCLP.format(precioDiario))
                    }

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Días de arriendo:")
                        Text("$diasArriendo días")
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Total a Pagar:",
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formaCLP.format(totalPagar),
                            style      = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color      = Color(0xFF1565C0) // Azul más oscuro
                        )
                    }
                }
            }

            // Aviso de pago y tiempo de retiro
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF3E0), // Naranjo clarito para alerta
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color(0xFFF57C00)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "El pago se realiza de forma presencial.\nTienes un plazo de 48 hrs para realizar el retiro en sucursales Notdel. En caso de no retirar el dispositivo" +
                                " el arriendo se cancelara de forma automatica.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // --- 5. BOTÓN CONFIRMAR ---
            Button(
                onClick = onConfirmar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar Reserva", fontSize = 18.sp)
            }

        }







    }


}