package cl.duoc.casasembrujadas.exception;

import java.time.LocalDateTime;
import java.util.Map;

// Forma ÚNICA de todas las respuestas de error de la API, para que el frontend siempre sepa qué leer:
// { "estado": 400, "error": "...", "mensaje": "...", "campos": { "correo": "..." }, "fecha": "..." }
// "record" es una clase de datos inmutable de Java (crea constructor, getters, equals y toString solo)
public record ErrorRespuesta(int estado, String error, String mensaje, Map<String, String> campos, LocalDateTime fecha) {

    public static ErrorRespuesta de(int estado, String error, String mensaje) {
        return new ErrorRespuesta(estado, error, mensaje, null, LocalDateTime.now());
    }
}
