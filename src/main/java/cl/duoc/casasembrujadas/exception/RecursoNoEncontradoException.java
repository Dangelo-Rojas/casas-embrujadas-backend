package cl.duoc.casasembrujadas.exception;

// Se lanza cuando se pide algo que no existe (ej: propiedad id 99). El manejador global la convierte en HTTP 404
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
