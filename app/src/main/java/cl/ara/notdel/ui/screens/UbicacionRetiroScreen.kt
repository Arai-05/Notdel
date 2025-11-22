package cl.ara.notdel.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionRetiroScreen() {

    val miUbicacion = LatLng(-33.49936500787212, -70.61654033901539)
    val nombreLugar1 = "Punto de Retiro 1"
    val lugar1 = LatLng(-33.497672632070476, -70.6126025410391)
    val nombreLugar2 = "Punto de Retiro 2"
    val lugar2 = LatLng(-33.50104607891704, -70.61707122623334)
    val nombreLugar3 = "Oficina Central"
    val lugar3 = LatLng(-33.49774554586376, -70.6178305190539)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(miUbicacion, 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Nuestros Puntos de Retiro") })
        }
    ) { paddingValues ->

        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = lugar1),
                title = nombreLugar1
            )
            Marker(
                state = MarkerState(position = lugar2),
                title = nombreLugar2
            )
            Marker(
                state = MarkerState(position = lugar3),
                title = nombreLugar3
            )
        }
    }
}