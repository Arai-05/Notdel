package cl.ara.notdel.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionRetiroScreen() {
    val context = LocalContext.current

    // Configuracion del OSM donde guarda el cache de mapas en el celular
    val sharedPrefs = context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
    Configuration.getInstance().load(context, sharedPrefs)
    Configuration.getInstance().userAgentValue = context.packageName // Esto evita bloqueos del servidor

    // Datos de las sucursales (pense crear un archivo aparte para esto pero me dio flojera la verdad)
    data class SucursalData(val nombre: String, val lat: Double, val lon: Double, val descripcion: String)

    val listaSucursales = listOf(
        SucursalData("Sucursal Norte", -33.468596, -70.648461, "Sgto. Aldea 1051-1093"),
        SucursalData("Sucursal Sur", -33.520926, -70.598625, "Calle Nueva 45"),
        SucursalData("Casa Matriz", -33.499746, -70.615929, "Av. Vicuña Mackenna")
    )

    val puntoInicialSantiago = GeoPoint(-33.499365, -70.616540)

    // Estados
    var sucursalSeleccionada  by remember { mutableStateOf<SucursalData?>(null) }
    var tienePermisoUbicacion by remember { mutableStateOf(false) }

    // Solicitar permiso de ubicacion
    val launcherPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        tienePermisoUbicacion = isGranted
    }

    LaunchedEffect(Unit) {
        // Pide permiso apenas carga la pantalla
        launcherPermiso.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Puntos de Retiro") }) },
        floatingActionButton = {
            // Boton flotante que solo aparece cuando se selecciona un marcador
            sucursalSeleccionada?.let { sucursal ->
                ExtendedFloatingActionButton(
                    text    = { Text("Ir a ${sucursal.nombre}") },
                    icon    = { Icon(Icons.Default.Navigation, contentDescription = "Navegar") },
                    onClick = {
                        // Logica para abrir maps o petal maps
                        val lat   = sucursal.lat
                        val lon   = sucursal.lon
                        val label = sucursal.nombre

                        // URI universal para los mapas
                        val uri    = Uri.parse("geo:$lat,$lon?q=$lat,$lon($label)")
                        val intent = Intent(Intent.ACTION_VIEW, uri)

                        // Intenta abrir google maps primero
                        intent.setPackage("com.google.android.apps.maps")

                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Si falla, abre el selector generico
                            val intentGenerico = Intent(Intent.ACTION_VIEW, uri)

                            try {
                                context.startActivity(intentGenerico)
                            } catch (e2: Exception) {
                                Toast.makeText(context, "No hay app de mapas instalada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->

        // Mapa
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK) // Estilo visual del mapa
                    setMultiTouchControls(true) // Zoom con dedos

                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

                    // Configs de optimizacion
                    isHorizontalMapRepetitionEnabled = false
                    isVerticalMapRepetitionEnabled   = false

                    // Zoom y centro inicial
                    controller.setZoom(15.0)
                    controller.setCenter(puntoInicialSantiago)

                    // Mi ubicacion (Punto azul)
                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    locationOverlay.enableMyLocation()
                    locationOverlay.enableFollowLocation()

                    // Punto azul (mi ubicacion)
                    val density = ctx.resources.displayMetrics.density
                    val circleSize = (20 * density).toInt() // Tamaño total
                    val bitmap = android.graphics.Bitmap.createBitmap(circleSize, circleSize, android.graphics.Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(bitmap)
                    val paint = android.graphics.Paint().apply { isAntiAlias = true }

                    // Dibja borde blanco
                    paint.color = android.graphics.Color.WHITE
                    paint.style = android.graphics.Paint.Style.FILL
                    canvas.drawCircle(circleSize / 2f, circleSize / 2f, circleSize / 2f, paint)

                    // Dibujar Centro Azul
                    paint.color = android.graphics.Color.parseColor("#4285F4")
                    canvas.drawCircle(circleSize / 2f, circleSize / 2f, (circleSize / 2f) - (3 * density), paint)

                    locationOverlay.setPersonIcon(bitmap)
                    locationOverlay.setDirectionIcon(bitmap)

                    overlays.add(locationOverlay)


                    // Marcadores de sucursales
                    listaSucursales.forEach { suc ->
                        val marcador = Marker(this)
                        marcador.position = GeoPoint(suc.lat, suc.lon)
                        marcador.title    = suc.nombre
                        marcador.snippet  = suc.descripcion
                        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                        val iconDrawable = ContextCompat.getDrawable(ctx, org.osmdroid.library.R.drawable.marker_default)
                        // Usa mutate() para no cambiar el color de todos los iconos del sistema, solo este
                        iconDrawable?.mutate()?.setTint(android.graphics.Color.parseColor("#6D45AD")) // Color Morado
                        marcador.icon = iconDrawable

                        // Accion al tocar un marcador
                        marcador.setOnMarkerClickListener { m, _ ->
                            m.showInfoWindow() // Muestra la burbuja
                            sucursalSeleccionada = suc // Actualiza el estado para mostrar el boton

                            // Animacion al centrar
                            controller.animateTo(marcador.position)
                            true
                        }
                        overlays.add(marcador)
                    }
                }
            },
            update = { map ->
                if (tienePermisoUbicacion) {
                    map.overlays.filterIsInstance<MyLocationNewOverlay>().firstOrNull()?.enableMyLocation()
                }
            }
        )
    }
}