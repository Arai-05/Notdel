package cl.ara.notdel.ui.screens

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.compose.foundation.border

@Composable
fun CamaraCarnetScreen(
    textoInstruccion: String,
    isProcesando: Boolean,
    onImagenCapturada: (ImageProxy) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // CONFIG DE LA CAMERA
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // 1. VARIABLE PARA GUARDAR LA REFERENCIA DE IMAGECAPTURE
    // Necesitamos esto para que el botón de abajo pueda "ver" la cámara configurada arriba
    var capturaImagen by remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {

        // VISTA PREVIA DE LA CAMARA
        AndroidView(
            factory = { ctx ->
                val vistaPrevia = PreviewView(ctx)
                vistaPrevia.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                val executor = ContextCompat.getMainExecutor(ctx)

                // Configuracion inicial de la camara
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Configuración de lo que se ve en pantalla
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(vistaPrevia.surfaceProvider)
                    }

                    // Configuracion del caso de uso de captura de imagen
                    val newCapturaImagen = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    // Guarda la referencia para usarla en el boton
                    capturaImagen = newCapturaImagen

                    // Selecciona la cámara trasera por defecto
                    val selectorCamara = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        // Limpiar configs previas
                        cameraProvider.unbindAll()

                        // Vincular la camara al ciclo de vida de la pantalla
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            selectorCamara,
                            preview,
                            newCapturaImagen
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, executor)

                vistaPrevia
            },
            modifier = Modifier.fillMaxSize()
        )

        // RECUADRO DEL CARNET
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Definir tamaño del recuadro (85% del ancho de la pantalla)
            val rectWidth = canvasWidth * 0.85f
            val rectHeight = rectWidth * 0.63f

            // Calcular posición para centrar el recuadro
            val left = (canvasWidth - rectWidth) / 2
            val top = (canvasHeight - rectHeight) / 2

            // Dibujar fondo negro semitransparente en toda la pantalla
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                size = size
            )

            // Recortar el agujero del centro
            // Borra el gris de fondo en la zona especifica
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(16.dp.toPx()), // Bordes redondeados
                style = Stroke(width = 4.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            // Dibuja borde blanco alrededor del recuadro
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(16.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        // Texto de instruccion arriba del recuadro
        Text(
            text = textoInstruccion,
            color = Color.White,
            style = typography.titleMedium,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp)
        ) {
            if (isProcesando) {
                // Si esta pensando muestra un circulo girando
                CircularProgressIndicator(modifier = Modifier.size(70.dp), color = Color.White)
            } else {
                // Si esta libre que muestre el boton para sacar la foto
                Button(
                    onClick = {
                        val captura = capturaImagen ?: return@Button // Si es nulo, no hace nada

                        val mainExecutor = ContextCompat.getMainExecutor(context)
                        captura.takePicture(
                            mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    onImagenCapturada(image)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Toast.makeText(context, "Error al capturar", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                    },
                    modifier       = Modifier.size(70.dp),
                    shape          = androidx.compose.foundation.shape.CircleShape,
                    colors         = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    // Círculo interior para que parezca botón de cámara
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.White, androidx.compose.foundation.shape.CircleShape)
                            .border(
                                2.dp,
                                Color.Black,
                                androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }
        }

        // Botón "X" para cerrar
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
        }
    }
}