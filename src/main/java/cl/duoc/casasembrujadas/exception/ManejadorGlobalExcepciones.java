package cl.duoc.casasembrujadas.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

// @RestControllerAdvice = manejo de errores CENTRALIZADO para todos los controladores.
// Así ningún controlador necesita try/catch, y todos los errores salen con el mismo formato JSON
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RecursoNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorRespuesta.de(404, "No encontrado", e.getMessage()));
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorRespuesta> credenciales(CredencialesInvalidasException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorRespuesta.de(401, "No autenticado", e.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorRespuesta> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.badRequest()
                .body(ErrorRespuesta.de(400, "Solicitud inválida", e.getMessage()));
    }

    // Falla un @Valid: devolvemos qué campo falló y por qué (igual que los mensajes bajo cada input del frontend)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> campos.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest()
                .body(new ErrorRespuesta(400, "Datos inválidos", "Revisa los campos marcados", campos, LocalDateTime.now()));
    }

    // JSON mal escrito o con tipos incorrectos (ej: "precio": "abc")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> jsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(ErrorRespuesta.de(400, "JSON inválido", "El cuerpo de la solicitud no tiene el formato esperado"));
    }

    // Un parámetro de la URL con el tipo incorrecto (ej: /api/propiedades/abc cuando se espera un número)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorRespuesta> tipoIncorrecto(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.badRequest()
                .body(ErrorRespuesta.de(400, "Parámetro inválido", "El valor '" + e.getValue() + "' no es válido para " + e.getName()));
    }

    // La base de datos rechazó la operación (ej: borrar una propiedad que ya está en pedidos)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorRespuesta> integridad(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorRespuesta.de(409, "Conflicto", "La operación rompe la integridad de los datos (el registro está siendo usado por otro)"));
    }
}
