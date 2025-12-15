package cl.ara.notdel.viewmodel

import android.app.Application
import androidx.camera.core.ImageProxy
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.Normalizer

enum class EtapaEscaneo {
    FRENTE,
    DORSO
}
class EscaneoViewModel(application: Application) : AndroidViewModel(application) {

    // Estado para saber si esta procesando o no
    private val _isProcesando = MutableStateFlow(false)
    val isProcesando = _isProcesando.asStateFlow()

    // Función auxiliar para limpiar acentos y poner mayusculas
    private fun normalizarTexto(texto: String): String {
        val normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
        return normalizado.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .uppercase()
    }

    // Palabras clave para identificar el carnet
    private val palabrasClaveCarnet = listOf("CHILE", "REPUBLICA", "APELLIDOS", "NOMBRES", "NACIONALIDAD", "SEXO")

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun analizarImagen(
        imagenProxy: ImageProxy,
        nombreUsuario: String,
        etapa: EtapaEscaneo,
        onResultado: (Boolean, String) -> Unit
    ) {
        _isProcesando.value = true
        val mediaImage = imagenProxy.image

        if (mediaImage != null) {
            val imagen = InputImage.fromMediaImage(mediaImage, imagenProxy.imageInfo.rotationDegrees)
            val detector = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            detector.process(imagen)
                .addOnSuccessListener { visionText ->
                    val textoLeidoCamara = normalizarTexto(visionText.text)
                    val nombreUsuarioLimpio = normalizarTexto(nombreUsuario)

                    if (etapa == EtapaEscaneo.FRENTE) {
                        // FRENTE
                        // Solo verifica que parezca un carnet (busca palabras clave)
                        val pareceCarnet = palabrasClaveCarnet.any { textoLeidoCamara.contains(it) }

                        if (pareceCarnet) {
                            onResultado(true, "Frente detectado correctamente")
                        } else {
                            onResultado(false, "No se puede detectar un carnet. Asegúrate de estar en un lugar iluminado.")
                        }

                    } else {
                        // DORSO
                        // Busca las flechitas típicas de atras
                        if (textoLeidoCamara.contains("<<")) {
                            // Busca el nombre del usuario dentro del código raro abajo del carnet
                            val partesNombre = nombreUsuarioLimpio.split(" ").filter { it.length > 2 }
                            val nombreCoincide = partesNombre.all { parte ->
                                textoLeidoCamara.contains(parte)
                            }

                            if (nombreCoincide) {
                                onResultado(true, "¡Identidad verificada exitosamente!")
                            } else {
                                onResultado(false, "El carnet no coincide con el nombre.")
                            }
                        } else {
                            onResultado(false, "No se puede detectar el dorso, intenta nuevamente.")
                        }
                    }
                }
                .addOnFailureListener { e ->
                    _isProcesando.value = false
                    imagenProxy.close()
                    onResultado(false, "Error al analizar la imagen")
                }
                .addOnCompleteListener {
                    _isProcesando.value = false
                    imagenProxy.close()
                }
        } else {
            _isProcesando.value = false
            imagenProxy.close()
            onResultado(false, "Error de camara")
        }
    }
}