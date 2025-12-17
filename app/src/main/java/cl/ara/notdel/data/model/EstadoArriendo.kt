package cl.ara.notdel.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
enum class EstadoArriendo(val textoPublico: String, val color: Color) {

    // DISPONIBLE (Verde)
    @SerializedName("disponible")
    DISPONIBLE("Disponible", Color(0xFF4CAF50)),

    // RESERVANDO (Amarillo - Alguien llenando formulario)
    @SerializedName("reservando")
    RESERVANDO("Reservado", Color(0xFFFFC107)),

    // POR RETIRAR (Naranjo - Ya pagado/firmado, falta ir a tienda)
    @SerializedName("por_retirar")
    POR_RETIRAR("No Disponible", Color(0xFFFF9800)),
    // Puse "No Disponible" para el publico general,
    // internamente se sabe que es "Por Retirar".

    // ARRENDADO (Rojo/Gris - En casa del cliente)
    @SerializedName("arrendado")
    ARRENDADO("Arrendado", Color(0xFFF44336)),

    // EN DEVOLUCION (Morado - Camino a tienda)
    @SerializedName("en_devolucion")
    EN_DEVOLUCION("No Disponible", Color(0xFF9C27B0));

}